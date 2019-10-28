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

    angular.module('CCC.AsmtPlayer').directive('cccAsmtTaskWarningModal', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "=",
                allowSkipInvalidTask: "=" // this flag allows the user to move onto the next question ( used when useSoftValidation is on and validateResponses is false )
            },

            controller: [

                '$scope',
                '$element',
                '$timeout',

                function ($scope, $element, $timeout) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    /*============ MODEL ===========*/

                    /*============ MODEL DEPENDENT METHODS ============*/


                    /*============ BEHAVIOR ============*/

                    $scope.stay = function () {
                        $scope.modal.instance.dismiss();
                    };

                    $scope.continue = function () {
                        $scope.modal.instance.close();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/
                }
            ],

            template: [

                // NOTE: the [autofocus] to ensure proper focus trapping
                '<div autofocus tabindex="0" id="{{modal.labelId}}">',

                    '<div class="modal-header">',

                        // NOTE: Here we add int he lableID
                        '<h3 class="modal-title text-warning">',
                            '<i class="fa fa fa-exclamation-triangle text-symbol-warning" aria-hidden="true"></i>',
                            ' <span translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.TITLE"></span>',
                        '</h3>',

                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<p translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.BODY" ng-if="allowSkipInvalidTask"></p>',
                            '<p translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.BODY_FORCE_STAY" ng-if="!allowSkipInvalidTask"></p>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div class="modal-footer" ng-if="allowSkipInvalidTask">',
                    '<button tabindex="0" class="btn btn-primary pull-left" ng-click="stay()">',
                        '<span translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.BUTTON_CANCEL"></span>',
                    '</button>',
                    '<button tabindex="0" class="btn btn-default" ng-click="continue()">',
                        '<span translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.BUTTON_CONFIRM"></span> ',
                        '<i class="fa fa-chevron-right"></i>',
                    '</button>',
                '</div>',

                '<div class="modal-footer" ng-if="!allowSkipInvalidTask">',
                    '<button tabindex="0" class="btn btn-primary" ng-click="stay()">',
                        '<span translate="CCC_PLAYER.VALIDATION.WARNING_MODAL.BUTTON_FORCE_STAY"></span> ',
                        '<i class="fa fa-chevron-right"></i>',
                    '</button>',
                '</div>'
            ].join('')

        };

    });

})();
