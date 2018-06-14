Feature: Mail cost changes based on priority, volume, weight, destination

    Scenario: Standard domestic mail delivery is less than air mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is less than domestic air mail

    Scenario: Domestic air mail costs more than standard
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic air mail
        Then the cost is more than domestic standard mail

    Scenario: Increased volume costs more
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 5m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $25

    Scenario: Increase weight costs more
        Given an initial map
        Given a parcel that weighs 5kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $25

    Scenario: Closer destination doesn't affect cost
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Wellington" "New Zealand"
        And I send the parcel to "Palmerston North" "New Zealand"
        And I send the parcel by domestic standard mail
        Then the cost is $5