Feature: checks if business features are correctly calculated

    Scenario: business revenue increases with mail delivered
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        And I send a domestic standard mail
        Then the total revenue is $1
        Then the total expenditure is $1
        Then the total volume is 1m3
        Then the total weight is 1kg
        Then the total number of items is 1
        Then the average delivery days is 1