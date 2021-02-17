package online.kheops.auth_server.album;

import online.kheops.auth_server.entity.Album;
import online.kheops.auth_server.entity.AlbumUser;
import online.kheops.auth_server.user.UserResponse;
import org.jooq.Record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static online.kheops.auth_server.util.JooqConstances.*;

public class AlbumResponseBuilder {

    private String id;
    private String name;
    private String description;
    private SortedSet<String> modalities;
    private LocalDateTime createdTime;
    private LocalDateTime lastEventTime;
    private Integer numberOfUsers;
    private List<UserResponse> users;
    private Integer numberOfComments;
    private Integer numberOfStudies;
    private Integer numberOfSeries;
    private Integer numberOfInstances;
    private Boolean addUser;
    private Boolean downloadSeries;
    private Boolean sendSeries;
    private Boolean deleteSeries;
    private Boolean addSeries;
    private Boolean writeComments;
    private Boolean isFavorite;
    private Boolean notificationNewSeries;
    private Boolean notificationNewComment;
    private Boolean isAdmin;

    public AlbumResponseBuilder() {
        users = new ArrayList<>();
    }

    public AlbumResponseBuilder(Album album, AlbumUser albumUser, long nbStudy, long nbSeries, long nbInstance, long nbUsers, long nbComment, String modalitiesLst) {
        users = new ArrayList<>();
        this.id = album.getId();
        this.name = album.getName();
        this.description = album.getDescription();
        this.createdTime = album.getCreatedTime();
        this.lastEventTime = album.getLastEventTime();
        this.numberOfUsers = ((Long)nbUsers).intValue();
        this.numberOfStudies = ((Long) nbStudy).intValue();
        this.numberOfSeries = ((Long)nbSeries).intValue();
        this.numberOfInstances = ((Long) nbInstance).intValue();
        this.addSeries = album.getUserPermission().isAddSeries();
        this.addUser = album.getUserPermission().isAddUser();
        this.deleteSeries = album.getUserPermission().isDeleteSeries();
        this.downloadSeries = album.getUserPermission().isDownloadSeries();
        this.sendSeries = album.getUserPermission().isSendSeries();
        this.writeComments = album.getUserPermission().isWriteComments();
        this.numberOfComments = ((Long)nbComment).intValue();
        this.isFavorite = albumUser.isFavorite();
        this.notificationNewComment = albumUser.isNewCommentNotifications();
        this.notificationNewSeries = albumUser.isNewSeriesNotifications();
        this.isAdmin = albumUser.isAdmin();

        this.modalities = new TreeSet<>();
        this.modalities.addAll(Arrays.asList(modalitiesLst.substring(1, modalitiesLst.length() - 1).split(",")));
        this.modalities.remove("NULL");
    }

    public AlbumResponseBuilder(Album album, long nbStudy, long nbSeries, long nbInstance, String modalitiesLst) {
        users = new ArrayList<>();
        this.id = album.getId();
        this.name = album.getName();
        this.description = album.getDescription();
        this.numberOfStudies = ((Long) nbStudy).intValue();
        this.numberOfSeries = ((Long)nbSeries).intValue();
        this.numberOfInstances = ((Long) nbInstance).intValue();

        this.modalities = new TreeSet<>();
        this.modalities.addAll(Arrays.asList(modalitiesLst.substring(1, modalitiesLst.length() - 1).split(",")));
        this.modalities.remove("NULL");
    }

    public AlbumResponseBuilder setAlbumFromUser(Record r) {
        this.id = r.getValue(ALBUM_ID).toString();
        this.name = r.getValue(ALBUM_NAME).toString();
        this.description = r.getValue(ALBUM_DESCRIPTION).toString();
        this.createdTime = (LocalDateTime) r.getValue(ALBUM_CREATED_TIME);
        this.lastEventTime = (LocalDateTime) r.getValue(ALBUM_LAST_EVENT_TIME);
        this.numberOfUsers = (Integer) r.getValue(NUMBER_OF_USERS);
        this.numberOfStudies = (Integer) r.getValue(NUMBER_OF_STUDIES);
        this.numberOfSeries = (Integer) r.getValue(NUMBER_OF_SERIES);
        try {
            this.numberOfInstances = ((BigDecimal) r.getValue(NUMBER_OF_INSTANCES)).intValue();
        } catch(NullPointerException e) {
            this.numberOfInstances = 0;
        }

        this.addSeries = (boolean) (r.getValue(ADD_SERIES_PERMISSION));
        this.addUser = (boolean) r.getValue(ADD_USER_PERMISSION);
        this.deleteSeries = (boolean) r.getValue(DELETE_SERIES_PERMISION);
        this.downloadSeries = (boolean) r.getValue(DOWNLOAD_USER_PERMISSION);
        this.sendSeries = (boolean) r.getValue(SEND_SERIES_PERMISSION);
        this.writeComments = (boolean) r.getValue(WRITE_COMMENT_PERMISSION);
        this.numberOfComments = (Integer) r.getValue(NUMBER_OF_COMMENTS);
        this.isFavorite = (boolean) r.getValue(FAVORITE);
        this.notificationNewComment = (boolean) r.getValue(NEW_COMMENT_NOTIFICATIONS);
        this.notificationNewSeries = (boolean) r.getValue(NEW_SERIES_NOTIFICATIONS);
        this.isAdmin = ((boolean) r.getValue(ADMIN));
        this.modalities = new TreeSet<>();
        this.modalities.addAll(Arrays.asList(r.getValue(MODALITIES).toString().split(",")));
        this.modalities.remove("NULL");
        return this;
    }

    public AlbumResponseBuilder addUser(AlbumUser albumUser) {
        UserResponse userResponse = new UserResponse(albumUser);
        users.add(userResponse);
        return this;
    }

    public AlbumResponse build() { return new AlbumResponse(this); }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public SortedSet<String> getModalities() {
        return modalities;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getLastEventTime() {
        return lastEventTime;
    }

    public Integer getNumberOfUsers() {
        return numberOfUsers;
    }

    public List<UserResponse> getUsers() {
        return users;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public Integer getNumberOfStudies() { return numberOfStudies; }

    public Integer getNumberOfSeries() { return numberOfSeries; }

    public Integer getNumberOfInstances() { return numberOfInstances; }

    public Boolean getAddUser() {
        return addUser;
    }

    public Boolean getDownloadSeries() {
        return downloadSeries;
    }

    public Boolean getSendSeries() {
        return sendSeries;
    }

    public Boolean getDeleteSeries() {
        return deleteSeries;
    }

    public Boolean getAddSeries() {
        return addSeries;
    }

    public Boolean getWriteComments() {
        return writeComments;
    }

    public Boolean getFavorite() {
        return isFavorite;
    }

    public Boolean getNotificationNewSeries() {
        return notificationNewSeries;
    }

    public Boolean getNotificationNewComment() {
        return notificationNewComment;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }
}
