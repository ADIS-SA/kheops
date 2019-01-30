package online.kheops.proxy.ohif;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.net.URI;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
class SeriesDTO {
    @XmlTransient
    private final URI rootURI;

    @XmlTransient
    private final SortedMap<Integer, InstanceDTO> instanceMap;

    @XmlElement(name = "instances")
    Collection<InstanceDTO> getInstances() {
        return instanceMap.values();
    }

    @XmlElement
    private final String seriesInstanceUid;

    @XmlElement
    private final String seriesDescription;

    private SeriesDTO() {
        throw new UnsupportedOperationException();
    }

    SeriesDTO(final URI rootURI, final Attributes attributes) {
        this.rootURI = rootURI;
        instanceMap = new TreeMap<>();

        seriesInstanceUid = attributes.getString(Tag.SeriesInstanceUID);
        seriesDescription = attributes.getString(Tag.SeriesDescription);

        addInstance(attributes);
    }

    void addInstance(final Attributes attributes) {
        instanceMap.computeIfAbsent(attributes.getInt(Tag.InstanceNumber, 0), instanceNumber -> InstanceDTO.from(rootURI, attributes));
    }
}
