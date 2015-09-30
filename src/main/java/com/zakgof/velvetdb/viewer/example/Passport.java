package com.zakgof.velvetdb.viewer.example;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
public class Passport implements Serializable {

  private String number;
  
  private LocalDate issueDate;
  
  private LocalDate expiryDate;
  
  public Passport(String passportNo) {
    this.number = passportNo;
    this.issueDate = LocalDate.of(1970, 1, 1);
    this.expiryDate = issueDate.plusYears(10);
  }

  @Override
  public String toString() {
    return number + " expires " + expiryDate;
  }
  
}
