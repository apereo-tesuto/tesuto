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

    angular.module('CCC.View.Common').directive('cccPrivacyStatement', function () {

        return {

            template: [

                '<ccc-view-manager-static-view page-title="Privacy Statement">',
                    '<p>This website is for demonstration purposes only, here is a previously developer Privacy Statement. The Tesuto is committed to protecting your privacy and the personal information collected via this website. This privacy policy applies solely to this website and does not apply to any other websites that you may be able to access from this website, each of which may have data collection, storage and use practices and policies that differ materially from this privacy policy. By using this website, you agree to this privacy policy, and consent to the data practices described in this policy.</p>',
                    '<p><strong>Minimum age:</strong> This website is not directed at children who are under 13 years of age, and the Tesuto and Apereo does not knowingly collect personally identifiable information from children under 13 years of age. You may not create an account if you are under 13 years of age, but you may utilize those portions of this website that do not involve the collection of personal information from users.</p>',
                    '<p><strong>Information collected:</strong> Tesuto has access to information that the Tesuto and Apereo collects through the OpenCCC Online Student Account and CCCApply Online Application for Admission, which is the following personal information:</p>',
                    '<ul>',
                        '<li>Name</li>',
                        '<li>Permanent Address</li>',
                        '<li>Mailing Address</li>',
                        '<li>Parent/Guardian</li>',
                        '<li>Birth Date</li>',
                        '<li>E-mail address</li>',
                        '<li>Telephone number</li>',
                        '<li>Social security number</li>',
                        '<li>Education</li>',
                    '</ul>',
                    '<p><strong>Use of data:</strong> Tesuto has access to the data Tesuto and Apereo collects in order to complete and submit Community College admission applications and financial aid on your behalf. In addition to data collected within Tesuto, Tesuto uses data collected to enable display of Education Planning information relevant to you in a timely manner, and to contact you when necessary in connection with these services. Tesuto and Apereo is authorized to collect information for this purpose by California Education Code Sections 68041 and 70901(b)(7).</p>',
                    '<p>Any information acquired by Tesuto and Apereo, is subject to the limitations set forth in the Information Practices Act of 1977 (Title 1.8 (commencing with Section 1798) of Part 4 of Division 3 of the Civil Code).</p>',
                    '<p><strong>Sharing of information:</strong> It is the policy of Tesuto and Apereo to share your personal information only as strictly necessary to provide you the specified services. This includes disclosing your personal information to colleges to which you apply. The Tesuto and Apereo could also be required to disclose the personal information that you provide in the following circumstances:</p>',
                    '<ul>',
                        '<li><p>In response to Public Records Act request, as allowed by the Information Practices Act and the Family Education and Pupil Rights Act.</p></li>',
                        '<li><p>To another government agency as required by state or federal law.</p></li>',
                        '<li><p>In response to a court or administrative order, a subpoena, or a search warrant.</p></li>',
                        '<li><p>To another state agency or to a public law enforcement organization in any case where the security of a network operated by Tesuto and Apereo and exposed directly to the Internet has been, or is suspected of having been, breached.</p></li>',
                        '<li><p>In the event of a conflict between this Policy and the Public Records Act, the Information Practices Act or other law governing the disclosure of records, the applicable law will control.</p></li>',
                    '</ul>',
                    '<p><strong>Individual choice:</strong> Submission of your personal information to this website is voluntary. If you choose not to submit your personal information to this website you will be required to complete paper forms to submit a college admission application, financial aid application and other student services applications.</p>',
                    '<p><strong>Accessing, modifying and deleting your information:</strong> You have the right to access the records containing your personal information. You may modify or delete from the data stored on this website any personal information you previously provided. Your records can be accessed by logging into your account or by contacting OpenCCC Technical Support. If you wish to delete your data without reuse or redistribution, please contact</p>',
                    '<p><strong>OpenCCC Technical Support at:</strong></p>',
                    '<div><p>Tel: <a href="tel:1-480-558-2400">1-480-558-2400</a><br/>',
                    'Email:&nbsp;<a href="mailto:support@openccc.net">support@openccc.net</a></p></div>',
                    '<p>Please be advised, once an application has been submitted to a college on your behalf, it becomes part of the records of the college to which it was submitted. If you believe there is an error on a submitted application, you must contact the college to which it was submitted for correction.</p>',
                    '<p><strong>Use of cookies:</strong> This website uses &ldquo;cookies&rdquo; to help personalize your online experience. A cookie is a text file that is placed on your hard disk by a web page server. Cookies cannot be used to run programs or deliver viruses to your computer. Cookies are uniquely assigned to you, and can only be read by a web server in the domain that issued the cookie to you.</p>',
                    '<p>One of the primary purposes of cookies is to provide a convenience feature to save you time. The purpose of a cookie is to tell the web server that you have returned to a specific page. For example, if you open an account, a cookie helps the Tesuto and Apereo recall your specific information on subsequent visits. This simplifies the process of recording your personal information, such as billing addresses. When you return to the same website the information you previously provided can be retrieved so you can easily use the features that you customized.</p>',
                    '<p>You have the ability to accept or decline cookies. Most web browsers automatically accept cookies, but you can usually modify your browser setting to decline cookies if you prefer. If you choose to decline cookies, you may not be able to fully experience the interactive features of the services or web sites you visit.</p>',
                    '<p><strong>Security of your personal information:</strong> Tesuto and Apereo takes reasonable precautions to protect the security, integrity and privacy of personal information of individuals collected or maintained by Tesuto and Apereo. The Tesuto and Apereo uses encryption software to protect the security of individuals&rsquo; personal information during transmission of such information through this website . The Tesuto and Apereo secures the personally identifiable information you provided on computer services in a controlled, secure environment, protected from unauthorized access, use or disclosures.</p>',
                    '<p><strong>Contact:</strong> For further information regarding this privacy policy, please contact:</p>',
                    '<div>Apereo Directly<br/>',
                    'Technology, Research and Information Systems Division<br/>',
                    'Aperoe Home Office<br/>',
                    'Street Address Not available<br/>',
                    'Hoboken, NJ 00000</div>',
                '</ccc-view-manager-static-view>',

            ].join('')
        };
    });

})();
