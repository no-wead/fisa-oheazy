package com.fisa.wooriarte.matching.repository;

import com.fisa.wooriarte.matching.domain.Matching;
import com.fisa.wooriarte.matching.domain.MatchingStatus;
import com.fisa.wooriarte.matching.domain.SenderType;
import com.fisa.wooriarte.projectItem.domain.ProjectItem;
import com.fisa.wooriarte.spaceItem.domain.SpaceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    List<Matching> findBySenderAndMatchingStatusAndSenderType(Long sender, MatchingStatus matchingStatus, SenderType senderType);
    List<Matching> findByReceiverAndMatchingStatusAndSenderType(Long receiver, MatchingStatus matchingStatus, SenderType senderType);
    Optional<Matching> findByProjectItemAndSpaceItem(ProjectItem projectItem, SpaceItem spaceItem);

    @Query(value = "select * from matching m where m.sender=:id and m.sender_type = 'SPACERENTAL' and (m.matching_status = 'WAITING' or m.matching_status = 'PREPARING' or m.matching_status = 'FINISH')", nativeQuery = true)
    List<Matching> findSuccessMatchingSenderSpaceRental(Long id);

    @Query(value = "select * from matching m where m.receiver=:id and m.sender_type = 'PROJECTMANAGER' and (m.matching_status = 'WAITING' or m.matching_status = 'PREPARING' or m.matching_status = 'FINISH')", nativeQuery = true)
    List<Matching> findSuccessMatchingReceiverSpaceRental(Long id);

    @Query(value = "select * from matching m where m.sender=:id and m.sender_type = 'PROJECTMANAGER' and (m.matching_status = 'WAITING' or m.matching_status = 'PREPARING' or m.matching_status = 'FINISH')", nativeQuery = true)
    List<Matching> findSuccessMatchingSenderProjectManager(Long id);

    @Query(value = "select * from matching m where m.receiver=:id and m.sender_type = 'SPACERENTAL' and (m.matching_status = 'WAITING' or m.matching_status = 'PREPARING' or m.matching_status = 'FINISH')", nativeQuery = true)
    List<Matching> findSuccessMatchingReceiverProjectManager(Long id);

}
