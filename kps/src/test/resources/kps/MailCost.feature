Feature: Mail cost changes based on priority, volume, weight, destination

    Scenario: Baseline priority, volume, weight, destination
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

#TODO in crease priority
    Scenario: Increased priority
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5

    Scenario: Increased volume
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 5m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $25

    Scenario: Increase weight
        Given an initial map
        Given a parcel that weighs 5kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $25

    Scenario: Closer destination
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Palmerston North" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5