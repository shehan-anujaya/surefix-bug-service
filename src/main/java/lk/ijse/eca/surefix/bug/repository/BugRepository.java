package lk.ijse.eca.surefix.bug.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import lk.ijse.eca.surefix.bug.entity.Bug;

public interface BugRepository extends JpaRepository<Bug, Long>, JpaSpecificationExecutor<Bug> {

    @Query("select b.status, count(b) from Bug b group by b.status")
    List<Object[]> countByStatus();

    @Query("select b.severity, count(b) from Bug b group by b.severity")
    List<Object[]> countBySeverity();
}
