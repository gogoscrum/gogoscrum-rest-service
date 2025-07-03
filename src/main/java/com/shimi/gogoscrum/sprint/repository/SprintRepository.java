package com.shimi.gogoscrum.sprint.repository;

import com.shimi.gogoscrum.sprint.model.Sprint;
import com.shimi.gsf.core.repository.GeneralRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface SprintRepository extends GeneralRepository<Sprint> {
    @Query("select s from Sprint s where s.startDate <= CURRENT_TIMESTAMP and s.endDate >= :endingOfYesterday")
    List<Sprint> findAllActiveSprints(Date endingOfYesterday);
}
