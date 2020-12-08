package online.kheops.auth_server.webhook.delayedWebhook;

import online.kheops.auth_server.EntityManagerListener;
import online.kheops.auth_server.KheopsInstance;
import online.kheops.auth_server.album.UserNotMemberException;
import online.kheops.auth_server.entity.*;
import online.kheops.auth_server.webhook.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static online.kheops.auth_server.album.AlbumQueries.findAlbumsWithEnabledNewSeriesWebhooks;
import static online.kheops.auth_server.album.Albums.getAlbumUser;

public class DelayedWebhookImpl implements DelayedWebhook {

    @Inject
    KheopsInstance kheopsInstance;

    private static final Logger LOG = Logger.getLogger(DelayedWebhookImpl.class.getName());

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final int TIME_TO_LIVE = 10;//seconds

    private static DataStructure dataStructure = new DataStructure();

    public DelayedWebhookImpl() { /*empty*/ }

    public void addHashMapData(Study study, Series series, Album destination, boolean isInbox, Integer numberOfNewInstances, Source source, boolean isNewInDestination, boolean isSend) {
        scheduler.schedule(() -> addData(study, series, destination, isInbox, numberOfNewInstances, source, isNewInDestination, isSend), 0, TimeUnit.SECONDS);
    }

    private void addData(Study study, Series series, Album destination, boolean isInbox, Integer numberOfNewInstances, Source source, boolean isNewInDestination, boolean isSend) {
        dataStructure.put(scheduler.schedule(() -> callWebhook(study, source), TIME_TO_LIVE, TimeUnit.SECONDS), study, series, numberOfNewInstances, source, destination, isInbox, isNewInDestination, isSend);
    }

    private void callWebhook(Study studyIn, Source source) {

        final EntityManager em = EntityManagerListener.createEntityManager();
        final EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            final List<WebhookAsyncRequest> webhookAsyncRequests = new ArrayList<>();
            Study study = em.merge(studyIn);
            em.refresh(study);
            final StudySourceKey studySourceKey = new StudySourceKey(study, source);
            final DestinationSeries destinationSeries = dataStructure.get(studySourceKey);
            final Set<DestinationDetails> destinationDetails = destinationSeries.getKeys();

            final HashMap<Series, Integer> newUploadedSeries = getNewUploadedSeries(destinationSeries, destinationDetails, em);

            for (DestinationDetails details : destinationDetails) {
                final Album album = em.merge(details.getAlbum());
                final SeriesData seriesData = destinationSeries.get(details);
                Boolean isAdmin = null;
                try {
                    final AlbumUser albumUser = getAlbumUser(album, source.getUser(), em);
                    isAdmin = albumUser.isAdmin();
                } catch (UserNotMemberException e){ /*empty*/ }

                final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                        .setKheopsInstance(kheopsInstance.get())
                        .isAutomatedTrigger()
                        .setSource(source)
                        .isAdmin(isAdmin)
                        .setStudy(study)
                        .setDestination(album.getId());

                for (Series unmergedSeries : seriesData.getSeries()) {
                    Series series = em.merge(unmergedSeries);
                    em.refresh(series);

                    if(seriesData.get(series).isSendUpload()) {
                        newSeriesWebhookBuilder.isSent();
                        newSeriesWebhookBuilder.addSeries(series);
                    } else {
                        newSeriesWebhookBuilder.isUpload();
                        if (seriesData.get(series).isNewInDestination()) {
                            newSeriesWebhookBuilder.addSeries(series, series.getNumberOfSeriesRelatedInstances());
                        } else {
                            if (newUploadedSeries.get(series) != 0) {
                                newSeriesWebhookBuilder.addSeries(series, newUploadedSeries.get(series));
                            }
                        }
                    }
                }

                for (Map.Entry<Series, Integer> s: newUploadedSeries.entrySet()) {
                    if (album.containsSeries(s.getKey(), em) && !newSeriesWebhookBuilder.getSeries().contains(s.getKey())) {
                        newSeriesWebhookBuilder.addSeries(s.getKey(), s.getValue());
                    }
                }

                if (!newSeriesWebhookBuilder.getSeries().isEmpty()) {
                    final NewSeriesWebhook newSeriesWebhook = newSeriesWebhookBuilder.build();
                    for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                        final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                        newSeriesWebhookBuilder.getSeries().forEach(webhookTrigger::addSeries);
                        em.persist(webhookTrigger);
                        webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhook, webhookTrigger));
                    }
                }
            }

            //for all others albums that contains the study
            for (Album album : findAlbumsWithEnabledNewSeriesWebhooks(study.getStudyInstanceUID(), em)) {
                if (!destinationDetails.contains(new DestinationDetails(album))) {
                    final NewSeriesWebhook.Builder newSeriesWebhookBuilder = NewSeriesWebhook.builder()
                        .setKheopsInstance(kheopsInstance.get())
                        .isAutomatedTrigger()
                        .setSource(source)
                        .setStudy(study)
                        .setDestination(album.getId());
                    for (Map.Entry<Series, Integer> s : newUploadedSeries.entrySet()) {
                        if (album.containsSeries(s.getKey(), em)) {
                            newSeriesWebhookBuilder.addSeries(s.getKey(), s.getValue());
                        }
                    }
                    if (!newSeriesWebhookBuilder.getSeries().isEmpty()) {
                        final NewSeriesWebhook newSeriesWebhook = newSeriesWebhookBuilder.build();
                        for (Webhook webhook : album.getWebhooksNewSeriesEnabled()) {
                            final WebhookTrigger webhookTrigger = new WebhookTrigger(new WebhookRequestId(em).getRequestId(), false, WebhookType.NEW_SERIES, webhook);
                            newSeriesWebhookBuilder.getSeries().forEach(webhookTrigger::addSeries);
                            em.persist(webhookTrigger);
                            webhookAsyncRequests.add(new WebhookAsyncRequest(webhook, newSeriesWebhook, webhookTrigger));
                        }
                    }
                }
            }

            tx.commit();
            for (WebhookAsyncRequest webhookAsyncRequest : webhookAsyncRequests) {
                webhookAsyncRequest.firstRequest();
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING,"", e);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
            dataStructure.remove(studyIn, source);
        }
    }

    private HashMap<Series, Integer> getNewUploadedSeries(DestinationSeries destinationSeries, Set<DestinationDetails> destinationDetails, EntityManager em) {
        final HashMap<Series, Integer> newUploadedSeries = new HashMap<>();
        for (DestinationDetails details : destinationDetails) {
            final SeriesData seriesData = destinationSeries.get(details);
            for (Series unmergedSeries : seriesData.getSeries()) {
                Series series = em.merge(unmergedSeries);
                em.refresh(series);
                if(destinationSeries.get(details).get(series).getNumberOfNewInstances() != 0) {
                    newUploadedSeries.compute(series, (s, nb) -> (nb == null)
                            ? destinationSeries.get(details).get(series).getNumberOfNewInstances()
                            : nb + destinationSeries.get(details).get(series).getNumberOfNewInstances() );

                }
            }
        }
        return newUploadedSeries;
    }
}