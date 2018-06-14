Feature: Send via standard and air, overseas

    Scenario: Send mail to Sydney Australia with international standard mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Auckland" "New Zealand"
        And I send the parcel to "Sydney" "Australia"
        And I send the parcel by international standard mail
        Then the cost is $26

    Scenario: Send mail to Sydney Australia with international air mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Auckland" "New Zealand"
        And I send the parcel to "Sydney" "Australia"
        And I send the parcel by international air mail
        Then the cost is $27

    Scenario: Send mail to Auckland with international standard mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Sydney" "Australia"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by international air mail
        Then the cost is $27

    Scenario: Send mail to Auckland with international air mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Sydney" "Australia"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by international air mail
        Then the cost is $27

    Scenario: Send mail to Auckland with domestic standard mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Sydney" "Australia"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic standard mail
        Then this should produce an error

    Scenario: Send mail to Auckland with domestic air mail
        Given an initial map
        Given a parcel that weighs 1kg
        Given a parcel with volume 1m3
        And I send the parcel from "Sydney" "Australia"
        And I send the parcel to "Auckland" "New Zealand"
        And I send the parcel by domestic air mail
        Then this should produce an error