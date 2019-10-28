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

    angular.module('CCC.Assessment').factory('InteractionInlineChoiceFactory', [

        '$translate',
        'InteractionFactory',

        function ($translate, InteractionFactory) {

            /*============ PRIVATE STATIC VARIABLES AND METHODS ============*/

            var getValidateResponsesValidations = function () {

                var that = this;
                var validateResponseValidations = [];

                var inlineChoiceItemSelected = (that.values.length > 0 && $.trim(that.values[0]) !== "");

                if (!inlineChoiceItemSelected) {

                    var noChoiceError = {
                        error: 'interaction.inlineChoice.required.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.INLINE_CHOICE.VALIDATION.EMPTY').then(function (translation) {
                        noChoiceError.msg = translation;
                    });

                    validateResponseValidations.push(noChoiceError);
                }

                return validateResponseValidations;
            };

            var getAllowSkippingValidations = function () {

                var that = this;
                var allowSkippingValidations = [];

                // additionally, if there are no errors and no values, then add in the total min error too
                if (that.values.length === 0) {

                    var noChoiceError = {
                        error: 'interaction.inlineChoice.required.invalid',
                        msg: ''
                    };

                    $translate('CCC_ASSESSMENT.INTERACTION.INLINE_CHOICE.VALIDATION.EMPTY').then(function (translation) {
                        noChoiceError.msg = translation;
                    });

                    allowSkippingValidations.push(noChoiceError);
                }

                return allowSkippingValidations;
            };


            /*============ CLASS DECLARATION ============*/

            var InteractionInlineChoice = function (interactionData) {

                /*============= DEFAULTS =============*/

                var defaults = {

                    // QTI Properties: http://www.imsglobal.org/question/qtiv2p1/imsqti_infov2p1.html#element10321
                    required: true,

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
                    return true; // no model validation here
                };


                /*============ INITIALIZATION =============*/

                // now everthing is ready, so run some initial model coercsion and structure checking
                validateModel();
            };


            /*============ PUBLIC STATIC METHODS ============*/

            /*============ LISTENERS ============*/


            /*============ FACTORY INSTANCE PASSBACK ============*/

            return InteractionInlineChoice;

        }
    ]);

})();


