/*-------------------------------------------------------------------------------
# Copyright © 2019 by California Community Colleges Chancellor's Office
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License.  You may obtain a copy
# of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.
#------------------------------------------------------------------------------*/
(function () {

    /**
     * We pulled this out into a service because the wizard summary directive needed to be used outside the use of the wizard
     */
    angular.module('CCC.View.Home').service('SubjectAreaWizardService', [

        'MMSubjectAreaClass',

        function (MMSubjectAreaClass) {

            /*============ SERVICE DECLARATION ============*/

            var SubjectAreaWizardService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var booleanToYesNoMapper = function (value) {
                return value ? 'Yes' : 'No';
            };

            var getSteps = function (mode) {

                // These are the configurations for this wizard
                // This has all the logic to determine which steps should load and which fields they are associated with (to help keep the model clean when steps are removed)
                return [
                    {
                        stepLabel: 'Subject Area',
                        isRequired: true,

                        template: 'ccc-sa-wzd-step-meta-data',

                        fields: {
                            'title': {
                                fieldLabel: 'Name',
                                emptyValue: ''  // we put empty values here to match the defaults of the Subject Area Class
                            },
                            'competencyAttributes.competencyCode': {
                                fieldLabel: 'Competencies',
                                emptyValue: ''  // we put empty values here to match the defaults of the Subject Area Class
                            },
                            'sisCode': {
                                fieldLabel: function (model) {
                                    return (model instanceof MMSubjectAreaClass) ? 'SIS Subject Area Name' : 'SIS test name';
                                },
                                emptyValue: '',  // we put empty values here to match the defaults of the Subject Area Class
                                valueMapper: function (value) {
                                    if ($.trim(value)) {
                                        return value;
                                    } else {
                                        return '--';
                                    }
                                }
                            },
                            'usePrereqPlacementMethod': {
                                fieldLabel: 'Placement Method',
                                emptyValue: null,
                                valueMapper: function (value) {
                                    if (value === false) {
                                        return 'Exit Skills';
                                    } else if (value === true) {
                                        return 'Minimum Entry Skills';
                                    } else {
                                        return '?';
                                    }
                                }
                            }
                        }
                    },
                    {
                        stepLabel: 'ELA Placements',
                        isRequired: function (immutableData, model) {
                            return model.competencyAttributes.competencyCode === 'ENGLISH' || model.competencyAttributes.competencyCode === 'ESL';
                        },

                        template: 'ccc-sa-wzd-step-show-ela-placements',

                        fields: {
                            'competencyAttributes.showPlacementToNativeSpeaker': {
                                fieldLabel: 'Show placement to native',
                                valueMapper: booleanToYesNoMapper
                            },
                            'competencyAttributes.showPlacementToEsl': {
                                fieldLabel: 'Show placement to ELL',
                                valueMapper: booleanToYesNoMapper
                            }
                        }
                    },
                    {
                        stepLabel: 'Multiple Measures: Opt-in',
                        isRequired: function (immutableData, model) {
                            return !(model instanceof MMSubjectAreaClass);
                        },

                        cleanable: false,   // this step may not be needed, but the value should never be wiped if it isn't
                        template: 'ccc-sa-wzd-step-mm-opt-in',

                        fields: {
                            'competencyAttributes.optInMultiMeasure': {
                                fieldLabel: 'Use CCC Multiple Measures',
                                valueMapper: booleanToYesNoMapper
                            }
                        }
                    },
                    {
                        stepLabel: 'Multiple Measures',
                        isRequired: function (immutableData, model) {
                            return model.competencyAttributes.optInMultiMeasure === true;
                        },

                        steps: [
                            {
                                stepLabel: 'MM used for placement',
                                isRequired: true,

                                template: 'ccc-sa-wzd-step-mm-selection',

                                fields: {
                                    'competencyAttributes.placementComponentMmap': {
                                        fieldLabel: 'Use MMAP - High School Transcript Based',
                                        valueMapper: booleanToYesNoMapper
                                    }
                                }
                            },
                            {
                                stepLabel: 'Use self reported transcript data',
                                isRequired: true,

                                template: 'ccc-sa-wzd-step-mm-use-self-reported',

                                fields: {
                                    'competencyAttributes.useSelfReportedDataForMM': {
                                        fieldLabel: 'Use self-reported transcript data',
                                        valueMapper: booleanToYesNoMapper
                                    }
                                }
                            },
                            {
                                stepLabel: 'MMAP ELA Options',
                                isRequired: function (immutableData, model) {
                                    return model.competencyAttributes.competencyCode === 'ENGLISH' || model.competencyAttributes.competencyCode === 'ESL';
                                },

                                template: 'ccc-sa-wzd-step-mm-ela',

                                fields: {
                                    'competencyAttributes.highestLevelReadingCourse': {
                                        fieldLabel: 'Highest level reading course'
                                    }
                                }
                            },
                            {
                                stepLabel: 'MMAP Math Options',
                                isRequired: function (immutableData, model) {
                                    return model.competencyAttributes.competencyCode === 'MATH';
                                },

                                template: 'ccc-sa-wzd-step-mm-math',

                                fields: {
                                    'competencyAttributes.prerequisiteGeneralEducation': {
                                        fieldLabel: 'GE math prerequisite'
                                    },
                                    'competencyAttributes.prerequisiteStatistics': {
                                        fieldLabel: 'Statistics prerequisite'
                                    }
                                }
                            },
                            {
                                id: 'mmDecisionLogic',
                                stepLabel: 'MMAP Decision Logic',
                                isRequired: true,

                                template: 'ccc-sa-wzd-step-mm-decision-logic',

                                requiresValueMapper: true,

                                fields: {
                                    'competencyAttributes.mmDecisionLogic': {
                                        fieldLabel: 'Decision logic'
                                    }
                                }
                            },
                            {
                                id: 'mmComponentsLogic',
                                stepLabel: 'MMAP Components',
                                isRequired: true,

                                template: 'ccc-sa-wzd-step-mm-components-logic',

                                requiresValueMapper: true,

                                fields: {
                                    'competencyAttributes.mmPlacementLogic': {
                                        fieldLabel: 'Placement logic'
                                    },
                                    'competencyAttributes.mmAssignedPlacementLogic': {
                                        fieldLabel: 'Assigned placement logic'
                                    }
                                }
                            }
                        ]
                    },
                    {
                        stepLabel: 'No Placement Message',
                        isRequired: true,

                        template: 'ccc-sa-wzd-step-no-placement',

                        fields: {
                            'noPlacementMessage': {
                                fieldLabel: 'No Placement Message',
                                emptyValue: ''   // we put empty values here to match the defaults of the Subject Area Class
                            }
                        }
                    },
                    {
                        stepLabel: 'Summary',
                        isRequired: true,

                        template: 'ccc-sa-wzd-step-summary',
                        width: 'large',

                        nextButton: {title: mode === 'create' ? 'Add Subject Area' : 'Update Subject Area', iconClass: 'fa fa-check-circle'},

                        fields: {}
                    }
                ];

            };


            /*============ SERVICE DEFINITION ============*/

            SubjectAreaWizardService = {
                getSteps: getSteps // NOTE, pass in mode for customization of some of the i18n
            };


            /*============ LISTENERS ============*/


            /*============ SERVICE PASSBACK ============*/

            return SubjectAreaWizardService;

        }
    ]);

})();
