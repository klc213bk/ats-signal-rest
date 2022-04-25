package com.klc213.ats.signal.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.klc213.ats.signal.bean.PivotPoint;


public class StatisticsUtils {

	
	public static PivotPoint pivotPoint(BigDecimal high, BigDecimal low, BigDecimal close) {
		BigDecimal p = high.add(high).add(low).add(close).divide(BigDecimal.valueOf(3.0), RoundingMode.HALF_EVEN);
		BigDecimal r1 = p.multiply(BigDecimal.valueOf(2.0)).subtract(low);
		BigDecimal r2 = p.add(high.subtract(low));
		BigDecimal s1 = p.multiply(BigDecimal.valueOf(2.0)).subtract(high);
		BigDecimal s2 = p.subtract(high.subtract(low));
		
		PivotPoint pivotPoint = new PivotPoint();
		pivotPoint.setPivot(p);
		pivotPoint.setResistance1(r1);
		pivotPoint.setResistance2(r2);
		pivotPoint.setSupport1(s1);
		pivotPoint.setSupport2(s2);
		
		return pivotPoint;
	}
	
	
}
