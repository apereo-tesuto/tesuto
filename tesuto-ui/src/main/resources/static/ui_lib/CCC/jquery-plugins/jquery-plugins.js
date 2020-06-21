(function($) {
    function hasScroll(el, index, match) {
        var $el = $(el),
            sX = $el.css('overflow-x'),
            sY = $el.css('overflow-y'),
            hidden = 'hidden', // minifiers would like this better
            visible = 'visible',
            scroll = 'scroll',
            axis = match[3]; // regex for filter -> 3 == args to selector

        if (!axis) { // better check than undefined
            //Check both x and y declarations
            if (sX === sY && (sY === hidden || sY === visible)) { //same check but shorter syntax
                return false;
            }
            if (sX === scroll || sY === scroll) { return true; }
        } else if (axis === 'x') { // don't mix ifs and switches on the same variable
            if (sX === hidden || sX === visible) { return false; }
            if (sX === scroll) { return true; }
        } else if (axis === 'y') {
            if (sY === hidden || sY === visible) { return false; }
            if (sY === scroll) { return true };
        }

        //Compare client and scroll dimensions to see if a scrollbar is needed

        return $el.innerHeight() < el.scrollHeight || //make use of potential short circuit
            $el.innerWidth() < el.scrollWidth; //innerHeight is the one you want
    }
    $.expr[':'].hasScroll = hasScroll;
})(jQuery);