package lk.ijse.eca.surefix.bug.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import lk.ijse.eca.surefix.bug.entity.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {
    List<Bug> findAllByOrderByCreatedAtDesc();
}
