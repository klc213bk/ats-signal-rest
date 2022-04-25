package com.klc213.ats.signal.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.klc213.ats.common.AtsBar;
import com.klc213.ats.signal.DataInsufficientException;
import com.klc213.ats.signal.utils.StatisticsUtils;

public class PriceSignal {

	private List<AtsBar> bars;

	private Map<Integer, Double> maMap; // moving average(period, ma)
	
	public PriceSignal() {
		bars = new ArrayList<>();
		maMap = new HashMap<>();
	}

	public void add(AtsBar atsBar) {
		bars.add(atsBar);
	}
	public int getBarSize() {
		return bars.size();
	}
	public AtsBar getLastBar() {
		if (getBarSize() > 1) {
			return bars.get(getBarSize() - 1);
		} else {
			throw new DataInsufficientException("No data available");
		}
	}
	public Double getSMA(int period) {
		if (maMap.containsKey(period)) {
			int barSize = bars.size();
			double newAvg = maMap.get(period) + 
					(bars.get(barSize - 1).getClose().doubleValue() - bars.get(barSize - 1 - period).getClose().doubleValue()) / 5;
			maMap.put(period, newAvg);
			return newAvg;
		} else {
			if (bars.size() < period) {
				throw new DataInsufficientException("data insufficient, data size < " + period);
			} else if (bars.size() == period) {
				double total = 0.0;
				for (int i = 0; i< period; i++) {
					total += bars.get(i).getClose().doubleValue();
				}
				double avg = total / period;
				maMap.put(period, avg);
				return avg;
			} else {
				double total = 0.0;
				for (int i = bars.size() - 1; i < 0; i--) {
					total += bars.get(i).getClose().doubleValue();
				}
				double avg = total / period;
				maMap.put(period, avg);
				return avg;
			}
		}
 		
	}
	
}
