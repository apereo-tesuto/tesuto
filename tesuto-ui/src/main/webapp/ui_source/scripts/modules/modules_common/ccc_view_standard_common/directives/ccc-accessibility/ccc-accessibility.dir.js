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

    angular.module('CCC.View.Common').directive('cccAccessibility', function () {

        return {

            template: [

                '<ccc-view-manager-static-view page-title="Accessibility">',
                    '<div class="row">',
                        '<div class="col-xs-9">',
                            '<h1>Web Accessibility</h1>',
                            '<p>Tesuto is committed to making its online assessment tool accessible to individuals of all abilities. Tesuto is developed to be in compliance with California Government Code 11135, which requires such technologies to meet the accessibility requirements of Section 508 of the federal Rehabilitation of 1973, as amended (29 U.S.C. Sec. 794d). Our goal is to make the Tesuto assessment accessible to everyone, including individuals with disabilities.</p>',
                            '<p>Web platforms and services are evolving constantly as is the support for assistive computer technologies. We welcome your feedback for improving the usability and accessibility of web applications and services. Please share your feedback or concerns with&nbsp;<a href="mailto:info@apereo.org" target="_blank">info@apereo.org</a><wbr />&nbsp;so we may improve the service for all participants.</p>',
                            '<h2>Keyboard Commands</h2>',
                            '<p>Web browsers provide support for a variety of keyboard commands to support navigation and interaction without requiring the use of a mouse. The following table includes common keyboard commands:</p>',
                            '<table class="table table-striped">',
                                '<tbody>',
                                    '<thead>',
                                        '<tr>',
                                            '<th>Keystroke</th>',
                                            '<th>Windows</th>',
                                            '<th>Mac OS X</th>',
                                        '</tr>',
                                    '</thead>',
                                    '<tr>',
                                        '<td>Increase Text Size</td>',
                                        '<td>Control+Plus Sign</td>',
                                        '<td>Command+Plus Sign</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Decrease Text Size</td>',
                                        '<td>Control+Minus Sign</td>',
                                        '<td>Command+Minus Sign</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Restore Text to Default</td>',
                                        '<td>Control+0</td>',
                                        '<td>Command+0</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Move forward through form fields</td>',
                                        '<td>Tab</td>',
                                        '<td>Tab</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Move backward through form fields</td>',
                                        '<td>Shift+Tab</td>',
                                        '<td>Shift+Tab</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Go back a page</td>',
                                        '<td>Alt+Left Arrow</td>',
                                        '<td>Command+Left Arrow</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Go forward a page</td>',
                                        '<td>Alt+Right Arrow</td>',
                                        '<td>Command+Right Arrow</td>',
                                    '</tr>',
                                    '<tr>',
                                        '<td>Close the window</td>',
                                        '<td>Control+W</td>',
                                        '<td>Command+W</td>',
                                    '</tr>',
                                '</tbody>',
                            '</table>',
                            '<p>Additional keyboard commands may be found for the following supported web browsers:</p>',
                            '<ul>',
                                '<li><a href="https://support.mozilla.org/en-US/kb/keyboard-shortcuts-perform-firefox-tasks-quickly" target="_blank">Firefox</a></li>',
                                '<li><a href="https://support.google.com/chrome/answer/157179?hl=en" target="_blank">Chrome</a></li>',
                                '<li><a href="https://support.apple.com/kb/PH17148?locale=en_US" target="_blank">Safari</a></li>',
                                '<li><a href="http://windows.microsoft.com/en-us/windows/internet-explorer-keyboard-shortcuts#1TC=windows-7" target="_blank">Internet Explorer</a></li>',
                            '</ul>',
                            '<h2>Reporting an Issue</h2>',
                            '<p>Tesuto strives to test various assistive technologies and web browsers to ensure a positive user experience. However, some assistive technology and web browser combinations may function better than others. If you cannot access content or use a feature due to a disability, please report the issue to:</p>',
                            '<div>CCC Technology Center<br/>',
                            'E-mail:&nbsp;<a href="mailto:info@apereo.org" target="_blank">accessibility@cccnext.<wbr />net</a></br>',
                            'Phone: <a href="tel:1-480-558-2400">1-480-558-2400</a></br>',
                            'TTY: <a href="tel:(855)%20350-3320">855-350-3320</a></br>',
                            '1 Apereo Drive</br>',
                            'Hoboken, NJ 00001</div>',
                            '<p>Helpful feedback may include:</p>',
                            '<ul>',
                                '<li>What you were trying to do</li>',
                                '<li>What technologies you may have been using</li>',
                                '<li>The barrier or issue you encountered</li>',
                                '<li>The web address where you experienced the issue</li>',
                            '</ul>',
                            '<p>For more information about providing feedback, please see&nbsp;<a href="http://www.w3.org/WAI/users/inaccessible" target="_blank">Contacting Organizations about Inaccessible Websites</a>&nbsp;from the Web Accessibility Initiative (WAI) for tips regarding what information you could provide so we can make improvements.</p>',
                        '</div>',
                    '</div>',
                '</ccc-view-manager-static-view>',

            ].join('')
        };
    });

})();
