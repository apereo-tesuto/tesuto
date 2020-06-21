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

    angular.module('CCC.Components').service('DateService', [

        function () {

            /*============ SERVICE DECLARATION ============*/

            var DateService;


            /*============ PRIVATE METHODS AND VARIABLES ============*/

            var MONTH_NAME_MAP = [
                {
                    month: 1,
                    titles: ['jan', 'january']
                },
                {
                    month: 2,
                    titles: ['feb', 'february']
                },
                {
                    month: 3,
                    titles: ['mar', 'march']
                },
                {
                    month: 4,
                    titles: ['apr', 'april']
                },
                {
                    month: 5,
                    titles: ['may']
                },
                {
                    month: 6,
                    titles: ['jun', 'june']
                },
                {
                    month: 7,
                    titles: ['jul', 'july']
                },
                {
                    month: 8,
                    titles: ['aug', 'august']
                },
                {
                    month: 9,
                    titles: ['sept', 'september']
                },
                {
                    month: 10,
                    titles: ['oct', 'october']
                },
                {
                    month: 11,
                    titles: ['nov', 'november']
                },
                {
                    month: 12,
                    titles: ['dec', 'december']
                }
            ];

            var MATCH_DATE_SEPARATORS_REGEX = /([-|\/|\.|\s+]+)/g;

            // this will force any combination of spaces, hyphens, slashes, and periods into a single slash for moment to parse
            var normalizeSeparators = function (dateString) {
                return dateString.replace(MATCH_DATE_SEPARATORS_REGEX, '/');
            };

            // from: https://gist.github.com/andrei-m/982927
            var levenshteinDistance = function(a, b) {

                if(a.length === 0) {
                    return b.length;
                }
                if(b.length === 0) {
                    return a.length;
                }

                var matrix = [];

                // increment along the first column of each row
                var i;
                for(i = 0; i <= b.length; i++){
                    matrix[i] = [i];
                }

                // increment each column in the first row
                var j;
                for(j = 0; j <= a.length; j++){
                    matrix[0][j] = j;
                }

                // Fill in the rest of the matrix
                for(i = 1; i <= b.length; i++){
                    for(j = 1; j <= a.length; j++){
                        if(b.charAt(i-1) === a.charAt(j-1)){
                            matrix[i][j] = matrix[i-1][j-1];
                        } else {
                            matrix[i][j] = Math.min(matrix[i-1][j-1] + 1, // substitution
                            Math.min(matrix[i][j-1] + 1, // insertion
                            matrix[i-1][j] + 1)); // deletion
                        }
                    }
                }

                return matrix[b.length][a.length];
            };

            var getMatchingMonthDigits = function (monthString_in) {

                var monthString = monthString_in.toLowerCase();

                var scoredMonths = [];
                _.each(MONTH_NAME_MAP, function (monthConfig) {

                    _.each(monthConfig.titles, function (monthTitle) {
                        scoredMonths.push({
                            month: monthConfig.month,
                            score: levenshteinDistance(monthTitle, monthString)
                        });
                    });
                });

                scoredMonths = _.sortBy(scoredMonths, function (monthScore) {
                    return monthScore.score;
                });

                // low threshold on levenshteinDistance allows for small typos like febuary to match => february
                if (scoredMonths[0].score < 2) {
                    return scoredMonths[0].month;
                } else {
                    return false;
                }
            };

            var getYearFromYearString = function (yearString) {

                // will turn two length into current year but with last two digits replaced with yearString
                if (yearString.length === 2) {

                    return (new Date().getFullYear() + '').slice(0, -2) + yearString;

                // if the year is greater than 3 digits, trust the user
                } else if (yearString.length > 3) {

                    return yearString;

                // otherwise no good
                } else {
                    return false;
                }
            };

            // all transform functions should return a date in 'mm/dd/yyyy' format
            // these are highlighy specific and should not be pulled into a global regex cache.. they are re-usable through this service anyway
            var DATE_PATTERNS = [
                {
                    id: 'mm/dd',
                    matcher: function (dateString) {
                        return dateString.match(/^(\d{1,2})(\/)(\d{1,2})$/);
                    },
                    transform: function (dateString) {
                        return dateString + '/' + new Date().getFullYear();
                    }
                },
                {
                    id: 'mm/dd/yyyy or mm/dd/yy',
                    matcher: function (dateString) {
                        return dateString.match(/^(\d{1,2})(\/)(\d{1,2})(\/)(\d{2,5})$/);
                    },
                    transform: function (dateString) {
                        var dateTokens = dateString.split('/');
                        var yearString = getYearFromYearString(dateTokens[2]);
                        if (yearString !== false) {
                            dateTokens[2] = yearString;
                            return dateTokens.join('/');
                        } else {
                            return null;
                        }
                    }
                },
                {
                    id: 'Sept 8th or Sept 8',
                    matcher: function (dateString) {
                        return dateString.match(/^([A-z]+)(\/)(\d{1,2})([A-z]{2})?$/i);
                    },
                    transform: function (dateString) {

                        var dateTokens = dateString.split('/');
                        dateTokens[0] = getMatchingMonthDigits(dateTokens[0]);

                        if (dateTokens[0] !== false) {

                            dateTokens[1] = dateTokens[1].replace(/([A-z]{2})?$/i, '');
                            dateTokens.push(new Date().getFullYear());

                            return dateTokens.join('/');

                        } else {
                            return null;
                        }
                    }
                },
                {
                    id: 'Sept 8th 1999 or September 8 2009 or Sept 3rd 15',
                    matcher: function (dateString) {
                        return dateString.match(/^([A-z]+)(\/)(\d{1,2})([A-z]{2})?(\/)(\d+)$/i);
                    },
                    transform: function (dateString) {

                        var dateTokens = dateString.split('/');

                        var monthDigits = getMatchingMonthDigits(dateTokens[0]);

                        if (monthDigits !== false) {

                            dateTokens[0] = monthDigits;
                            dateTokens[1] = dateTokens[1].replace(/([A-z]{2})?$/i, '');
                            dateTokens[2] = getYearFromYearString(dateTokens[2]);

                            if (dateTokens[2] !== false) {
                                return dateTokens.join('/');
                            } else {
                                return null;
                            }

                        } else {
                            return null;
                        }
                    }
                }
            ];

            var parseDateString = function (viewValue_in) {

                // remove trailing whitespace and normalize all seperators into '/'
                var viewValue = normalizeSeparators($.trim(viewValue_in));

                var dPattern;
                for (var i=0; i < DATE_PATTERNS.length; i++) {

                    dPattern = DATE_PATTERNS[i];

                    if (dPattern.matcher(viewValue)) {
                        return dPattern.transform(viewValue);
                    }
                }

                return null;
            };



            /*============ SERVICE DEFINITION ============*/

            DateService = {

                /**
                 * Attempt to use some bit of intelligence to parse loosly formatted date strings
                 * @type {string}
                 * @returns {string or null} will return a MM/dd/YYYY formatted string or null
                 */
                parseDateString: parseDateString
            };


            /*============ LISTENERS ============*/

            /*============ SERVICE PASSBACK ============*/

            return DateService;

        }
    ]);

})();
