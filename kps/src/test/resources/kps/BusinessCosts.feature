Feature: checks if business features are correctly calculated

    Scenario: check business figures increase correctly
        Given an initial map
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

    Scenario: check business figures after more than 1 package
        Given an initial map
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        And I send a domestic standard mail
        And I send a domestic standard mail
        Then the total revenue is $2
        Then the total expenditure is $2
        Then the total volume is 2m3
        Then the total weight is 2kg
        Then the total number of items is 2
        Then the average delivery days is 1
        Then the number of critical routes is 0

    Scenario: critical route is added when profit lost package is sent
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        And I a profit lost domestic standard mail
        Then the number of critical routes is 1

    