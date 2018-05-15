Feature: Delivery

  Scenario: Send some mail
     Given an initial map
     Given a parcel that weighs 1kg
     Given a parcel that measures 1000 cc
     And I send the parcel from "Wellington" "New Zealand" 
     And I send the parcel to "Palmerston North" "New Zealand" 
     And I send the parcel by domestic standard mail 
     Then the cost is $5


