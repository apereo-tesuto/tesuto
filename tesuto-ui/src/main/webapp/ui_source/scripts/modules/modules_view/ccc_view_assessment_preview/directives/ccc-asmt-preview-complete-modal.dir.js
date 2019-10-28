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

    angular.module('CCC.AsmtPlayer').directive('cccAsmtPreviewCompleteModal', function () {

        return {

            restrict: 'E',

            // you would typically want to pass in the modal instance so you can use it to self close etc..
            scope: {
                modal: "="
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

                    $scope.continue = function () {
                        $scope.modal.instance.close();
                    };

                    $scope.cancel = function () {
                        $scope.modal.instance.dismiss();
                    };


                    /*============ LISTENERS ===========*/

                    /*============ INITIALIZATION ===========*/

                }
            ],

            template: [

                // NOTE: [autofocus] should be used to ensure proper focus trap
                '<div autofocus tabindex="-1" id="{{modal.labelId}}">',

                    '<div class="modal-header">',

                        // NOTE: Here we add int he lableID
                        '<h3 class="modal-title">',
                            '<span translate="CCC_VIEW_ASSESSMENT_PREVIEW.COMPLETION_MODAL.TITLE"></span>',
                        '</h3>',

                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<p translate="CCC_VIEW_ASSESSMENT_PREVIEW.COMPLETION_MODAL.BODY"></p>',
                        '</div>',
                    '</div>',

                '</div>',

                '<div class="modal-footer">',
                    '<button tabindex="0" class="btn btn-default btn-submit-button" ng-click="cancel()">',
                        '<span translate="CCC_VIEW_ASSESSMENT_PREVIEW.COMPLETION_MODAL.BUTTON.CANCEL"></span> ',
                    '</button>',
                    '<button tabindex="0" class="btn btn-primary btn-submit-button" ng-click="continue()">',
                        '<span translate="CCC_VIEW_ASSESSMENT_PREVIEW.COMPLETION_MODAL.BUTTON.CONFIRM"></span> ',
                    '</button>',
                '</div>'
            ].join('')

        };

    });

})();
