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
     * InteractionChoice class factory
     */

    angular.module('CCC.Assessment').factory('InteractionChoiceFactory', [

        '$translate',
        'InteractionFactory',

        function ($translate, InteractionFactory) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var getValidateResponsesValidations = function () {

                var that = this;

                var validateResponseValidations = [];

                var meetsMinChoices = that.minChoices === 0 || (that.values.length >= that.minChoices);
                var meetsMaxChoices = that.maxChoices === 0 || (that.maxChoices > 0 && that.values.length <= that.maxChoices);

                if (meetsMinChoices && !meetsMaxChoices) {

                    var maxValidationError = {
                        error: 'interaction.choice.maxChoices.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.CHOICE.VALIDATION.MAX', {MAX: that.maxChoices}).then(function (translation) {
                        maxValidationError.msg = translation;
                    });

                    validateResponseValidations.push(maxValidationError);
                }

                if (!meetsMinChoices && meetsMaxChoices) {

                    var minValidationError = {
                        error: 'interaction.choice.minChoices.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.CHOICE.VALIDATION.MIN', {MIN: that.minChoices}, 'messageformat').then(function (translation) {
                        minValidationError.msg = translation;
                    });

                    validateResponseValidations.push(minValidationError);
                }

                return validateResponseValidations;
            };

            var getAllowSkippingValidations = function () {

                var that = this;
                var allowSkippingValidations = [];

                // for choice interaction forcing an answer means making them select at least one
                if (that.values.length === 0) {

                    var allowSkippingError = {
                        error: 'interaction.choice.minChoices.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.CHOICE.VALIDATION.MIN', {MIN: 1}, 'messageformat').then(function (translation) {
                        allowSkippingError.msg = translation;
                    });

                    allowSkippingValidations.push(allowSkippingError);
                }

                return allowSkippingValidations;
            };


            /*============ CLASS DECLARATION ============*/

            var InteractionChoice = function (interactionData) {

                /*============= DEFAULTS =============*/

                var defaults = {

                    // QTI Properties: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10278
                    minChoices: 1,
                    maxChoices: 0,
                    orientation: 'vertical', // ['vertical', 'horizontal']

                    // VALIDATION METHOD OVERRIDES
                    getAllowSkippingValidations: getAllowSkippingValidations,
                    getValidateResponsesValidations: getValidateResponsesValidations
                };


                /*=========== INSTANCE INIT AND CONFIG MERGE ============*/

                // Gather final configs and Extend the InteractionFactory base class
                var that = this;
                var interactionConfigs = $.extend(true, {}, defaults, interactionData);
                InteractionFactory.call(that, interactionConfigs);


                /*=========== PRIVATE VARIABLES AND METHODS =============*/

                var validateModel = function () {
                    // coerce min and max values into integers
                    that.maxChoices = parseInt(that.maxChoices);
                    that.minChoices = parseInt(that.minChoices);
                };


                /*============ INITIALIZATION =============*/

                // now everthing is ready, so run some initial model coercsion and structure checking
                validateModel();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return InteractionChoice;

        }
    ]);

})();


