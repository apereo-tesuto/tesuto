
var MessageFormat = MessageFormat || {};

(function () {

    var english_locale = function (n) {
        if (n === 1) {
            return 'one';
        }
        return 'other';
    };

    MessageFormat.locale['none'] = english_locale;
    MessageFormat.locale['es'] = english_locale;

})();

