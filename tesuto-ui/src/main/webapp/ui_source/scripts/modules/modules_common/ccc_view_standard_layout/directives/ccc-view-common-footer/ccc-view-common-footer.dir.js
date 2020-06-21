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
     * DIRECTIVE FOR A "STANDARD" VIEW FOOTER
     */

    angular.module('CCC.View.Layout').directive('cccViewCommonFooter', function () {

        return {

            restrict: 'E',

            transclude: true,

            template: [
                '<footer class="app-footer" role="contentinfo">',
                    '<div class="container">',
                        '<div class="row">',
                            '<div class="col-md-4">',
                                '<img class="footer-logo" src="ui/resources/images/ccc-logo-gray.png" alt="California Community Colleges Chancellor\'s Office logo">',
                            '</div>',
                            '<div class="col-md-8">',
                                '<div class="footer-links">',
                                    '<ul class="list-inline">',
                                        '<li><a href="#" ui-sref="termsOfUse"><span translate="CCC_VIEW_STANDARD_LAYOUT.FOOTER.TERMS_OF_USE"></span></a></li>',
                                        '<li><a href="#" ui-sref="privacyStatement"><span translate="CCC_VIEW_STANDARD_LAYOUT.FOOTER.PRIVACY_STATEMENT"></a></li>',
                                        '<li><a href="#" ui-sref="accessibility"><span translate="CCC_VIEW_STANDARD_LAYOUT.FOOTER.ACCESSIBILITY"></a></li>',
                                    '</ul>',
                                    '<p><span translate="CCC_VIEW_STANDARD_LAYOUT.FOOTER.COPYRIGHT"></p>',
                                '</div>',
                            '</div>',
                        '</div>',
                    '</div>',
                '</footer>'
            ].join('')

        };

    });

})();
