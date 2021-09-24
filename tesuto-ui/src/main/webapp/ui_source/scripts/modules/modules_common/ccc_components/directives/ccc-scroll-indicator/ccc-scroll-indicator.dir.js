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

    angular.module('CCC.Components').directive('cccScrollIndicator', function () {

        return {

            restrict: 'A',

            controller: [

                '$scope',
                '$element',
                '$timeout',
                '$compile',

                function ($scope, $element, $timeout, $compile) {

                    /*============ PRIVATE VARIABLES AND METHODS ============*/

                    var moreContainerElement;
                    var wrapperElement;

                    var scrollToTop = function () {
                        $($element).scrollTop(0);
                    };


                    /*============ MODEL ============*/


                    /*============ LISTENERS ============*/

                    $scope.$on('ccc-scroll-indicator.scrollToTop', scrollToTop);


                    /*============ INITIALIZATION ============*/

                    // give all related directives that ability to wire up before adding in new markup
                    $timeout(function () {

                        wrapperElement = $('<div class="ccc-scroll-indicator-parent"></div>');

                        moreContainerElement = $([
                            '<div class="ccc-scroll-indicator-container" role="presentation">&nbsp;</div>'
                        ].join(''));

                        // plop in the moreContainer Element and compile it
                        $($element).wrap(wrapperElement);
                        $($element).after(moreContainerElement);
                        $compile(moreContainerElement)($scope);

                    }, 1);

                }
            ]

        };

    });

})();

