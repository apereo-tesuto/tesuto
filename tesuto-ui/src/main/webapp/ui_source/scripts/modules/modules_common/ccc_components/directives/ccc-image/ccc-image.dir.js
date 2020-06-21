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

    angular.module('CCC.Components').directive('cccImage', function () {

        return {

            restrict: 'E',

            scope: {
                imageUrl: '@',
                defaultUrl: '@',    // the defaultUrl if provided will be used if imageUrl is falsy or there is a detected error loading imageUrl
                width: '@?'         // you can say auto which will put no restriction on size, or provide a width value like 25, 100% etc..
            },

            controller: [

                '$scope',
                '$element',

                function ($scope, $element) {

                    /*============ PRIVATE VARIABLES ===========*/

                    var imageUrl = ($scope.imageUrl !== 'undefined' && $scope.imageUrl !== 'null' && $scope.imageUrl !== 'false') ? $scope.imageUrl : false;
                    var defaultUrl = ($scope.defaultUrl !== 'undefined' && $scope.defaultUrl !== 'null' && $scope.defaultUrl !== 'false') ? $scope.defaultUrl : false;

                    var injectImage = function (target, src, defaultSrc) {

                        var imageLoaded = function (img) {
                            $element.addClass('ccc-image-loaded');
                        };

                        var imageError = function (img) {
                            img.onerror = $.noop;

                            if (defaultSrc) {
                                img.src = defaultSrc;
                            }
                        };

                        var img = document.createElement('img');

                        if ($scope.width && $scope.width !== 'auto') {
                            $(img).attr('width', $scope.width);
                        }

                        var prop = img.naturalWidth === undefined ? 'naturalWidth' : 'width';

                        $(target).append(img);

                        if (src) {
                            img.src = src;
                        } else if (defaultSrc) {
                            img.src = defaultSrc;
                        }

                        if (img.complete) {

                            if (img[prop]) {
                                imageLoaded(img);
                            } else {
                                imageError(img);
                            }

                        } else {

                            img.onload = function () {
                                imageLoaded (img);
                            };
                            img.onerror = function () {
                                imageError (img);
                            };
                        }
                    };


                    /*============ MODEL =============*/

                    /*============ BEHAVIOR ============*/


                    /*============ INITIALIZATION ============*/

                    injectImage($element, imageUrl, defaultUrl);
                }
            ]

        };

    });

})();
