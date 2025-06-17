package org.kosa.tripTalk.travellog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TravelLogService {
		@Autowired
		private TravelLogRepository travelLogRepository;
		
		public TravelLog write(TravelLog travelLog) {
			return travelLogRepository.save(travelLog);
			//return 
		}

}


