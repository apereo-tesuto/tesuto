This folder contains the drl files for the CAL-pas decision trees as well as 
the official CCCAssess logic for determination of Placements from placementComponents
and Assigned Placements from a set of placements. In addition it contains the csv files required 
to fill in the tokens and validate each of the rulesets, as well as the accepted title and description
for the ruleset.

Expected files:
rules:
CAL-Pass-Plus-math.drtl
CAL-Pass-Plus-english.drl
CAL-Pass-Plus-esl.drl
CAL-Pass-Plus-reading.drl
CCCAssess-placement.drl
CCCAssess-assigned-placement.drl

title-description:
CAL-Pass-Plus-math-title-description.txt
CAL-Pass-Plus-english-title-description.txt
CAL-Pass-Plus-esl-title-description.txt
CAL-Pass-Plus-reading-title-description.txt
CCCAssess-placement-title-description.txt
CCCAssess-assigned-placement-title-description.txt

tokens:
CAL-Pass-Plus-math-tokens.csv
CAL-Pass-Plus-english-tokens.csv
CAL-Pass-Plus-esl-tokens.csv
CAL-Pass-Plus-reading-tokens.csv
CCCAssess-placement-tokens.drl
CCCAssess-assigned-placement-tokens.drl

validation:
CAL-Pass-Plus-math-validation.csv
CAL-Pass-Plus-english-validation.csv
CAL-Pass-Plus-esl-validation.csv
CAL-Pass-Plus-reading-validation.csv
CCCAssess-Plus-placement-validation.csv
CCCAssess-Plus-assigned-placement-validation.csv

Application:
tesuto

Events Supported:
MULTIPLE_MEASURE_PLACEMENT,
PLACEMENT,
ASSIGNED_PLACEMENT

Categories For Searching and Filtering:
mmComponentPlacmentLogic,
mmAssignedPlacementLogic,
mmPlacementLogic

