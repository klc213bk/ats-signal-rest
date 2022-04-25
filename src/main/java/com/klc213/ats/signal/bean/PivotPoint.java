package com.klc213.ats.signal.bean;

import java.math.BigDecimal;

public class PivotPoint {

	private BigDecimal pivot;
	
	private BigDecimal resistance1;
	
	private BigDecimal resistance2;
	
	private BigDecimal support1;
	
	private BigDecimal support2;

	public BigDecimal getPivot() {
		return pivot;
	}

	public void setPivot(BigDecimal pivot) {
		this.pivot = pivot;
	}

	public BigDecimal getResistance1() {
		return resistance1;
	}

	public void setResistance1(BigDecimal resistance1) {
		this.resistance1 = resistance1;
	}

	public BigDecimal getResistance2() {
		return resistance2;
	}

	public void setResistance2(BigDecimal resistance2) {
		this.resistance2 = resistance2;
	}

	public BigDecimal getSupport1() {
		return support1;
	}

	public void setSupport1(BigDecimal support1) {
		this.support1 = support1;
	}

	public BigDecimal getSupport2() {
		return support2;
	}

	public void setSupport2(BigDecimal support2) {
		this.support2 = support2;
	}
	
	
}
