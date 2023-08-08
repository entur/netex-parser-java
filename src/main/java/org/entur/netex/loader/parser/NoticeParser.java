package org.entur.netex.loader.parser;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.Notice;
import org.rutebanken.netex.model.NoticeAssignment;
import org.rutebanken.netex.model.NoticeAssignmentsInFrame_RelStructure;
import org.rutebanken.netex.model.NoticesInFrame_RelStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Parse a Notice and Notice Assigment, used only be the {@link ServiceFrameParser} and
 * {@link TimeTableFrameParser}.
 */
class NoticeParser {
    private static final Logger LOG = LoggerFactory.getLogger(NoticeParser.class);

    private final Collection<Notice> notices = new ArrayList<>();
    private final Collection<NoticeAssignment> noticeAssignments = new ArrayList<>();


    void parseNotices(NoticesInFrame_RelStructure notices) {
        if (notices == null) return;

        this.notices.addAll(notices.getNotice());
    }

    void parseNoticeAssignments(NoticeAssignmentsInFrame_RelStructure na) {
        if (na == null) return;

        for (JAXBElement<? extends DataManagedObjectStructure> it : na.getNoticeAssignment_()) {
            NoticeAssignment noticeAssignment = (NoticeAssignment) it.getValue();
            boolean error = false;

            if(noticeAssignment.getNoticedObjectRef() == null) {
                LOG.warn(
                        "Notice assignment is missing 'noticedObjectRef'. Id: {}",
                        noticeAssignment.getId()
                );
                error = true;
            }
            if(noticeAssignment.getNoticeRef() == null && noticeAssignment.getNotice() == null) {
                LOG.warn(
                        "Notice assignment have no 'notice' or 'noticeRef'. Id: {}",
                        noticeAssignment.getId()
                );
                error = true;
            }
            if(!error) {
                this.noticeAssignments.add(noticeAssignment);
            }
        }
    }

    void setResultOnIndex(NetexEntitiesIndex index) {
        // update entities
        index.getNoticeIndex().putAll(notices);
        index.getNoticeAssignmentIndex().putAll(noticeAssignments);
    }
}
