package org.kosa.tripTalk.travellog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelLogRepository extends JpaRepository<TravelLog,Long>{

	//public String write();
	//TravelLog write(TravelLog travelLog);
	TravelLog save(TravelLog travelLog);
	
	

}
