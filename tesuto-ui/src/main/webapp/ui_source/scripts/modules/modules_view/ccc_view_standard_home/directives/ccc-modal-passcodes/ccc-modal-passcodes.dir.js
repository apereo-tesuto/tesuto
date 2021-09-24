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

    angular.module('CCC.View.Home').directive('cccModalPasscodes', function () {

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

                    $scope.cancel = function () {
                        $scope.modal.instance.dismiss();
                    };


                    /*============ LISTENERS ===========*/

                    $scope.$on('ccc-proctor-passcodes.confirmHidden', function () {
                        $element.find('[autofocus]').focus();
                    });


                    /*============ INITIALIZATION ===========*/

                    // automatically close modal after 2 minutes
                    $timeout(function () {
                        $scope.modal.instance.dismiss();
                    }, 120000);

                }
            ],

            template: [

                // NOTE: [autofocus] for modals to ensure focus trap
                '<div id="{{modal.labelId}}">',

                    '<div class="modal-header">',
                    '<button autofocus ng-click="cancel()" type="button" class="close" aria-label="Close"><span aria-hidden="true">×</span></button>',
                        '<h3 class="modal-title" translate="CCC_VIEW_HOME.MODAL.PASSCODES.TITLE"></h3>',
                    '</div>',

                    '<div class="modal-content-wrapper">',
                        // NOTE: Here we add in the contentID
                        '<div class="modal-body" id="{{modal.contentId}}">',
                            '<ccc-proctor-passcodes></ccc-proctor-passcodes>',
                        '</div>',
                    '</div>',
                '</div>'

            ].join('')

        };

    });

})();
