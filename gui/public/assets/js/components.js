/*!
 * Telekom Components v2.2.4 [2018-04-19]
 * Copyright 2014-2018 Deutsche Telekom AG
 */

if (typeof jQuery === 'undefined') {
    throw new Error('jQuery is required')
}

// http://paulirish.com/2011/requestanimationframe-for-smart-animating/
// http://my.opera.com/emoller/blog/2011/12/20/requestanimationframe-for-smart-er-animating

// requestAnimationFrame polyfill by Erik Möller. fixes from Paul Irish and Tino Zijdel

// MIT license

+function () {
    var lastTime = 0;
    var vendors = ['ms', 'moz', 'webkit', 'o'];
    for (var x = 0; x < vendors.length && !window.requestAnimationFrame; ++x) {
        window.requestAnimationFrame = window[vendors[x] + 'RequestAnimationFrame'];
        window.cancelAnimationFrame = window[vendors[x] + 'CancelAnimationFrame']
            || window[vendors[x] + 'CancelRequestAnimationFrame'];
    }

    if (!window.requestAnimationFrame)
        window.requestAnimationFrame = function (callback, element) {
            var currTime = new Date().getTime();
            var timeToCall = Math.max(0, 16 - (currTime - lastTime));
            var id = window.setTimeout(function () {
                    callback(currTime + timeToCall);
                },
                timeToCall);
            lastTime = currTime + timeToCall;
            return id;
        };

    if (!window.cancelAnimationFrame)
        window.cancelAnimationFrame = function (id) {
            clearTimeout(id);
        };
}(window.jQuery);
+function ($) {
    'use strict';

    // DETECT MOBILE DEVICE SUPPORT
    // ============================================================

    // Adapted from http://www.detectmobilebrowsers.com
    var ua = navigator.userAgent || navigator.vendor || window.opera

    // Checks for iOs, Android, Blackberry, Opera Mini, and Windows mobile devices
    var ismobile = (/iPhone|iPod|iPad|Silk|Android|BlackBerry|Opera Mini|IEMobile/).test(ua)

    $(function () {
        $.support.mobile = ismobile
    })

}(window.jQuery);

/* ========================================================================
 * Bootstrap: transition.js v3.3.1
 * http://getbootstrap.com/javascript/#transitions
 * ========================================================================
 * Copyright 2011-2014 Twitter, Inc.
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
 * ======================================================================== */

+function ($) {
    'use strict';

    // CSS TRANSITION SUPPORT (Shoutout: http://www.modernizr.com/)
    // ============================================================

    function transitionEnd() {
        var el = document.createElement('telekomcomponents')

        var transEndEventNames = {
            WebkitTransition: 'webkitTransitionEnd',
            MozTransition: 'transitionend',
            OTransition: 'oTransitionEnd otransitionend',
            transition: 'transitionend'
        }

        for (var name in transEndEventNames) {
            if (el.style[name] !== undefined) {
                return {end: transEndEventNames[name]}
            }
        }

        return false // explicit for ie8 (  ._.)
    }

    // http://blog.alexmaccaw.com/css-transitions
    $.fn.emulateTransitionEnd = function (duration) {
        var called = false
        var $el = this
        $(this).one('tcTransitionEnd', function () {
            called = true
        })
        var callback = function () {
            if (!called) $($el).trigger($.support.transition.end)
        }
        setTimeout(callback, duration)
        return this
    }

    $(function () {
        $.support.transition = transitionEnd()

        if (!$.support.transition) return

        $.event.special.tcTransitionEnd = {
            bindType: $.support.transition.end,
            delegateType: $.support.transition.end,
            handle: function (e) {
                if ($(e.target).is(this)) return e.handleObj.handler.apply(this, arguments)
            }
        }
    })

}(jQuery);

+function ($) {
    'use strict';

    var formset = '.form-radio-set'
    var Radio = function (element, options) {
        this.options =
            this.$element = null

        this.init(element, options)
    }

    Radio.DEFAULTS = {
        template: '<div tabindex="-1" class="form-radio-js" role="radio" autocomplete="off" hidefocus="true">' +
        '&nbsp;' + // &nbsp; important für Screenreader
        '<span class="border"></span>' +
        '<span class="check"></span>' +
        '</div>'
    }

    Radio.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        this.options = this.getOptions(options)
        var $radio = this.radio()
        var name = $e.attr('name')

        $e.before($radio)
        $e.addClass('hidden')

        $e.on('change.tc.radio', function (e) {
            if (name) {
                $('input[name="' + name + '"]:radio:enabled')
                    .not($e)
                    .trigger('deselect.tc.radio')
            }

            $radio.addClass('checked')
                .attr('aria-checked', 'true')
                .attr('tabindex', '0')
        })

        $e.on('deselect.tc.radio', function (e) {
            $radio.removeClass('checked')
                .attr('aria-checked', 'false')
                .attr('tabindex', '-1')
        })

        $radio.parents(formset).on('click.tc.radio', function (e) {
            if ($radio.is('.disabled, :disabled'))
                return;

            if (!$(e.target).hasClass('form-radio hidden'))
                $e.trigger('click');
        })

        $radio.on('keyup.tc.radio', function (e) {
            if ($radio.is(':focus')) $radio.addClass('focus')
        })

        $radio.on('keydown.tc.radio', function (e) {

            if (!name) return true

            if (e.altkey) {
                return true
            }

            var idx = $('input[name="' + name + '"]:radio:enabled').index($e)

            switch (e.keyCode) {
                case 37:
                case 38:
                    if (e.shiftKey) {
                        return true;
                    }

                    $('input[name="' + name + '"]:radio:enabled:eq(' + (idx - 1) + ')')
                        .trigger('click')
                        .prev()
                        .focus()

                    e.preventDefault();
                    e.stopPropagation();
                    return false;
                case 39:
                case 40:

                    if (e.shiftKey) {
                        return true;
                    }

                    $('input[name="' + name + '"]:radio:enabled:eq(' + (idx + 1) + ')')
                        .trigger('click')
                        .prev()
                        .focus()

                    e.preventDefault();
                    e.stopPropagation();
                    return false;
            }

            return true;
        })

        $radio.on('keypress.tc.radio', function (e) {
            if (e.altkey) {
                return true
            }

            if (e.keyCode === 37 || e.keyCode === 38 // left/up
                || e.keyCode === 39 || e.keyCode === 40) { // right/down
                e.stopPropagation()
                return false;
            }

            return true;
        })

        $radio.on('blur.tc.radio', function (e) {
            $radio.removeClass('focus')
        })

        if ($e.is(':checked')) {
            $radio.addClass('checked')
                .attr('aria-checked', 'true')
                .attr('tabindex', '0')
        }

        if ($e.is(':disabled') || $e.parents('fieldset').is(':disabled')) {
            this.disable()
        }

        // if it's a group, rest focus
        if (name) {
            var $radioGroup = $('input[name="' + name + '"]:radio:enabled')
            var $focusElem = $radioGroup.filter(':checked')

            if ($focusElem.length < 1) {
                $focusElem = $radioGroup.first()
            }

            $radioGroup.each(function (index, elem) {
                var $elem = $(elem)
                var data = $elem.data('tc.radio')
                if (!data) return
                var radio = data.radio()
                radio.attr('tabindex', $focusElem[0] == elem ? '0' : '-1')
            })
        }
    }

    Radio.prototype.disable = function () {
        var $e = this.$element
        var $radio = this.radio()

        $radio.attr('aria-disabled', true)
            .addClass('disabled')

        $e.parents(formset).addClass('disabled')
    }

    Radio.prototype.enable = function () {
        var $e = this.$element
        var $radio = this.radio()

        $radio.attr('aria-disabled', false)
            .removeClass('disabled')

        $e.parents(formset).removeClass('disabled')
    }

    Radio.prototype.getDefaults = function () {
        return Radio.DEFAULTS
    }

    Radio.prototype.getOptions = function (options) {
        options = $.extend({}, this.getDefaults(), this.$element.data(), options)
        return options
    }

    Radio.prototype.radio = function () {
        return this.$radio = this.$radio || $(this.options.template)
    }

    // RADIO PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.radio')
            var options = typeof option === 'object' && option

            if (!data) $this.data('tc.radio', (data = new Radio(this, options)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.radio

    $.fn.radio = Plugin
    $.fn.radio.Constructor = Radio


    // RADIO NO CONFLICT
    // ====================

    $.fn.radio.noConflict = function () {
        $.fn.radio = old
        return this
    }

}(window.jQuery);

+function ($) {
    'use strict';

    var formset = '.form-checkbox-set'
    var Checkbox = function (element, options) {
        this.options =
            this.$element = null

        this.init(element, options)
    }

    Checkbox.DEFAULTS = {
        template: '<div tabindex="0" role="checkbox" class="form-checkbox-js" autocomplete="off" hidefocus="true">' +
        '&nbsp;' + // important for screenreader
        '<span class="border"></span>' +
        '<span class="check" role="presentation"></span>' +
        '</div>'
    }

    Checkbox.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        this.options = this.getOptions(options)
        var $checkbox = this.checkbox()

        $e.before($checkbox)
        $e.addClass('hidden')

        $e.on('change.tc.checkbox', function (e) {
            if ($e.is(':checked')) {
                $checkbox.addClass('checked')
                    .attr('aria-checked', 'true')
            } else {
                $checkbox.removeClass('checked')
                    .attr('aria-checked', 'false')
            }
        })

        $checkbox.parents(formset).on('click.tc.checkbox', function (e) { // IE8 label click support
            if ($(e.target).is('label') || $checkbox[0] === $(e.target).closest('.form-checkbox-js')[0]) {
                $e.trigger('click')
                return false
            }

            return !$checkbox.is('.disabled, :disabled')
        })

        $checkbox.on('keyup.tc.checkbox', function (e) {
            if ($checkbox.is(':focus'))
                $checkbox.addClass('focus')
        })

        $checkbox.on('keydown.tc.checkbox', function (e) {
            if (e.altkey || e.ctrlKey || e.shiftKey) {
                return true
            }

            if (e.which == 32) {
                $checkbox.trigger('click')
                e.stopPropagation()
                return false
            }

            return true
        })

        $checkbox.on('keypress.tc.checkbox', function (e) {
            if (e.altkey || e.ctrlKey || e.shiftKey) {
                return true
            }

            if (e.which == 32) {
                e.stopPropagation()
                return false
            }

            return true
        })

        $checkbox.on('blur.tc.checkbox', function (e) {
            $checkbox.removeClass('focus')
        })

        if ($e.is(':checked')) {
            $checkbox.addClass('checked')
                .attr('aria-checked', 'true')
        }

        if ($e.attr('tabindex')) {
            $checkbox.attr('tabindex', $e.attr('tabindex'))
        }

        if ($e.is(':disabled') || $e.parents('fieldset').is(':disabled')) {
            this.disable()
        }
    }

    Checkbox.prototype.disable = function () {
        var $e = this.$element
        var $checkbox = this.checkbox()

        $checkbox.attr('aria-disabled', true)
            .attr('tabindex', '-1')
            .addClass('disabled')

        $e.parents(formset).addClass('disabled')
    }

    Checkbox.prototype.enable = function () {
        var $e = this.$element
        var $checkbox = this.checkbox()

        var tabindex = '0';
        if ($e.attr('tabindex')) {
            tabindex = $e.attr('tabindex')
        }

        $checkbox.attr('aria-disabled', false)
            .attr('tabindex', tabindex)
            .removeClass('disabled')

        $e.parents(formset).removeClass('disabled')
    }

    Checkbox.prototype.getDefaults = function () {
        return Checkbox.DEFAULTS
    }

    Checkbox.prototype.getOptions = function (options) {
        options = $.extend({}, this.getDefaults(), this.$element.data(), options)
        return options
    }

    Checkbox.prototype.checkbox = function () {
        return this.$checkbox = this.$checkbox || $(this.options.template)
    }

    // CHECKBOX PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.checkbox')
            var options = typeof option === 'object' && option

            if (!data)
                $this.data('tc.checkbox', (data = new Checkbox(this, options)))
            if (typeof option === 'string')
                data[option]()
        })
    }

    var old = $.fn.checkbox

    $.fn.checkbox = Plugin
    $.fn.checkbox.Constructor = Checkbox


    // CHECKBOX NO CONFLICT
    // ====================

    $.fn.checkbox.noConflict = function () {
        $.fn.checkbox = old
        return this
    }

}(window.jQuery);

+function ($, window) {
    'use strict';

    var formset = '.form-radio-set'
    var SelectBox = function (element, options) {
        this.options =
            this.$element = null

        this.init(element, options)
    }

    SelectBox.DEFAULTS = {
        template:
        '<div class="form-select-js" tabindex="-1">' +
        '<input type="text" class="form-select-js-choice-input" role="combobox" aria-autocomplete="list" aria-readonly="true" tabindex="0"/>' +
        '<button type="button" class="form-select-js-choice" tabindex="-1"></button>' +
        '<div class="form-select-js-option-scroll-wrapper" tabindex="-1">' +
        '<ul class="form-select-js-options" role="listbox" aria-expanded="true"></ul>' +
        '</div>' +
        '</div>',
        optionTemplate: '<li></li>',
        idPrefix: 'select-',
        maxItems: 25,
        appendToBody: true
    }

    SelectBox.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        this.options = this.getOptions(options)
        var $select = this.select()
        var $scrollwrapper = this.scrollwrapper()
        var $textinput = this.textinput()
        var $choice = this.choice()
        var $document = $(document)
        var selectboxPrototype = this

        // only selectbox can be replaced
        if (!$e.is('select')) return

        // only non multiple selectbox is supported
        if ($e.is('[multiple]')) return

        // only no sized selectbox is supported
        if ($e.is('[size]') && parseInt($e.attr('size')) > 1) {
            return
        }

        this.setIds()

        this.createOptions()

        // render element
        $e.before($select)

        // sync with original
        this.sync()
        $e.on('change', $.proxy(function (e) {
            this.sync()
        }, this))

        if ($e.is(':disabled')) {
            $select.attr('aria-disabled', true)
            $textinput.prop('disabled', true)
        }

        if ($.support.mobile) {
            return this.applyNativeBehaviour()
        } else {
            $e.addClass('hidden')
        }
        var originalId = $e.attr('id')
        if (originalId) {
            var orgLbl = $('[for="' + originalId + '"]')
            if (orgLbl) {
//        $select.attr('title', (orgLbl.attr('title') || (orgLbl.text() + ' Select')) + (orgLbl.attr('alt') ? ' '+orgLbl.attr('alt') : ''))
                if (!orgLbl.attr('id'))
                    orgLbl.attr('id', this.getId() + '-label')
                $textinput.attr('aria-labelledby', orgLbl.attr('id'))
            }
        }

        // hide all on document interaction
        var autoClose = function (e) {

            var $target = $(e.target).parents('.form-select-js')
            var $target2 = $(e.target).parents('.form-select-js-option-scroll-wrapper')

            if ($target[0] !== $select[0] && $target2[0] !== $scrollwrapper[0]) { // hide if not over this list of element
                return selectboxPrototype.hide(false); // do not focus after autoclose to prevent scroll issues
            }

            var $optionList = $(e.target).parent()
            if ($optionList.hasClass('form-select-js-options')) {

                var $scrollWarpper = $optionList.parent()
                var pos = $scrollWarpper.scrollTop()
                var delta = e.originalEvent.wheelDelta ? e.originalEvent.wheelDelta * -1 : e.originalEvent.delta
                var direction = (e.originalEvent.deltaY || e.originalEvent.wheelDelta * -1) > 0 ? 1 : -1
                var maxPos = $optionList.height() - $scrollWarpper.height()

                if (delta) {
                    var newPos = Math.max(0, Math.min(pos + delta, maxPos))
                    $scrollWarpper.scrollTop(newPos)

                    e.stopImmediatePropagation()
                    e.preventDefault()
                }

                if ((pos <= 0 && direction === -1) || (pos >= maxPos && direction === 1)) {
                    e.stopImmediatePropagation()
                    e.preventDefault()
                }
            }

        }

        $document.on('click touchstart mousewheel wheel scroll', autoClose)
        $choice.parents().on('mousewheel wheel scroll', autoClose)
        $(window).on('resize mousewheel wheel scroll', autoClose) // otherwise positions/dimensions will go crazy

        $choice.on('click', $.proxy(this.toggle, this))
    }

    SelectBox.prototype.setIds = function () {
        var id = this.getId()

        this.select().attr('id', id)
        this.optionlist().attr('id', id + '-list')
        this.textinput().attr('aria-owns', id + '-list')
            .attr('aria-labelledby', id + '-label')
    }

    // set original select on top so that mobile devices touch
    SelectBox.prototype.applyNativeBehaviour = function () {
        var $select = this.select()
        var $choice = this.choice()
        var pos = $select.position()
        var dimensions = {width: $select.width(), height: $select.height()}

        // apply native opening to new select
        $choice.on('click', $.proxy(this.openNative, this))

        // try to position native element over new
        this.$element.css({
            display: 'block',
            visibility: 'visible',
            position: 'absolute',
            top: pos.top,
            left: pos.left,
            width: dimensions.width,
            height: dimensions.height,
            opacity: 0,
            'z-index': 99999,
            '-webkit-appearance': 'menulist-button'
        })
    }

    SelectBox.prototype.openNative = function () {
        var elem = this.$element;
        if (document.createEvent) {
            var e = document.createEvent('MouseEvents')
            e.initMouseEvent('mousedown', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null)
            elem[0].dispatchEvent(e)
        } else if (elem[0].fireEvent) {
            elem[0].fireEvent('onmousedown')
        }
    }

    SelectBox.prototype.sync = function () {
        var $choice = this.choice()
        var $optionlist = this.optionlist()
        var $textinput = this.textinput()

        $choice.text(this.$element.find('option:selected').text())
        var oid = $optionlist.find('.selected').attr('id')
        $textinput.attr('aria-activedescendant', oid)
    }

    SelectBox.prototype.hoverOption = function ($option) {
        if ($option.attr('aria-disabled') === 'true') return
        $option.siblings().removeClass('hover')
        $option.addClass('hover')
    }

    SelectBox.prototype.selectOption = function ($option) {
        if ($option.attr('aria-disabled') === 'true') return
        $option.siblings().removeClass('selected')
        $option.addClass('selected')
    }

    SelectBox.suppressMouseInteractionAfterViewUpdate = false
    SelectBox.prototype.setSelectedOption = function ($option, force) {
        var $e = this.$element
        var $optionlist = this.optionlist()
        var $options = $optionlist.children()
        var $currentOption = $options.filter('.selected')

        if ($option !== $currentOption || force) {
            this.hoverOption($option)
            this.selectOption($option)
            $e[0].selectedIndex = $options.index($option)
            $e.trigger('change')

            this.updateScrollView()
            SelectBox.suppressMouseInteractionAfterViewUpdate = true
        }
    }

    SelectBox.prototype.hasOriginalOptions = function () {
        return this.getOriginalOptions().length > 0
    }

    SelectBox.prototype.getOriginalOptions = function () {
        return this.$element.children()
    }

    SelectBox.prototype.createOptions = function () {
        var self = this
        var $optionlist = this.optionlist()
        var $children = this.getOriginalOptions()
        var $textinput = this.textinput()
        var id = this.getId()
        var idcount = 0

        $optionlist.empty()
        $children.each(function () {
            var originalOption = $(this)
            var classes = []

            var disabled = originalOption.is(':disabled')
//      if (disabled) classes.push('disabled')
            if (originalOption.is(':selected')) classes.push('selected')

            $optionlist.append($(self.options.optionTemplate, {
                html: originalOption.html(),
                'data-value': originalOption.val(),
                'class': classes.join(' '), // class must be in quotes to prevent IE8 preserved keyword bug
                role: 'option',
                tabindex: '-1',
                id: id + '-option-' + (++idcount),
                'aria-disabled': disabled
            }))
        })

        var oid = $optionlist.find('.selected').attr('id')
        $textinput.attr('aria-activedescendant', oid)

        $optionlist.children()
            .on('mouseenter.tc.selectbox.option.data-api', $.proxy(function (e) {
                e.preventDefault()

                // avoid mouseselection while using arrowkeys while moving area
                if (SelectBox.suppressMouseInteractionAfterViewUpdate)
                    return SelectBox.suppressMouseInteractionAfterViewUpdate = false

                this.hoverOption($(e.currentTarget))
            }, this))
            .on('click.tc.selectbox.option.data-api', $.proxy(function (e) {
                e.preventDefault()
                if ($(e.currentTarget).is('[aria-disabled="true"]')) return
                this.setSelectedOption($(e.currentTarget), true)
                this.hide()
            }, this))
    }

    SelectBox.prototype.getVisibleItemsNum = function () {
        var $select = this.select()
        var $scrollwrapper = this.scrollwrapper()
        var rowHeight = this.measureRowHeight()
        var visibleItems = Math.min(this.options.maxItems, $scrollwrapper.find('li').length)
        var pageHeightMax = Math.min(($(window).height() - $select.outerHeight()) * 0.5, visibleItems * rowHeight)
        var visibleItemsMax = Math.floor(pageHeightMax / rowHeight)
        return Math.max(1, visibleItemsMax)
    }

    SelectBox.prototype.measureRowHeight = function () {
        var $scrollwrapper = this.scrollwrapper()
        return $scrollwrapper.find('li:first').outerHeight()
    }

    SelectBox.prototype.measureScrollwrapperDimensions = function () {
        var $choice = this.choice()
        var $scrollList = this.optionlist()
        var rowHeight = this.measureRowHeight()
        var totalHeight = Math.max(rowHeight, Math.min($scrollList.height(), this.getVisibleItemsNum() * rowHeight))
        var $scrollWrapper = $scrollList.parent()

        return {
            'min-width': $choice.outerWidth(),
            height: totalHeight + Number($scrollWrapper.css('border-top-width').replace(/px/, '')) + Number($scrollWrapper.css('border-bottom-width').replace(/px/, ''))
        }
    }

    SelectBox.prototype.getCalculatedScrollwrapperOffset = function () {
        var $choice = this.choice()
        var scrollwrapperDimensions = this.measureScrollwrapperDimensions()
        var offset = {left: $choice.offset().left}

        // placement
        if ($choice.offset().top + $choice.outerHeight() - $(window).scrollTop() + scrollwrapperDimensions.height > $(window).height()) {
            // above
            offset.top = $choice.offset().top - $(window).scrollTop() - scrollwrapperDimensions.height - ($choice.outerHeight() - $choice.height())
        } else {
            // below
            offset.top = $choice.offset().top + $choice.outerHeight() - $(window).scrollTop()
        }

        return offset
    }

    SelectBox.prototype.show = function () {
        var $select = this.select()
        var $textinput = this.textinput()
        var $scrollwrapper = this.scrollwrapper()
        var $optionlist = this.optionlist()

        if (this.isDisabled()) return

        if (!this.hasOriginalOptions()) return

        this.createOptions()

        $textinput.focus()
        $select.addClass('in')
        $optionlist.attr('aria-expanded', 'true')

        var dimensions = this.measureScrollwrapperDimensions()
        var offset = this.getCalculatedScrollwrapperOffset()

        // place scrollwrapper to body
        if (this.options.appendToBody) {
            $('body').append($scrollwrapper)
        }

        $scrollwrapper.css(dimensions)
            .css(offset) // set by css to prevent margin-top
            .css('display', 'block')

        this.updateScrollView()
    }

    SelectBox.prototype.isOpened = function () {
        return this.select().hasClass('in')
    }

    SelectBox.prototype.hide = function (focus) {
        var $select = this.select()
        var $textinput = this.textinput()
        var $scrollwrapper = this.scrollwrapper()

        if (this.isDisabled()) return

        if ($select.hasClass('in')) $select.removeClass('in')
        $scrollwrapper.css('display', '')

        // reappend to selectjs after use
        if (this.options.appendToBody) {
            $select.append($scrollwrapper)
        }

        var $optionlist = this.optionlist()
        $optionlist.attr('aria-expanded', 'false')
        if (focus !== false) $textinput.focus()
    }

    SelectBox.prototype.isDisabled = function () {
        return this.select().attr('aria-disabled') === 'true'
    }

    SelectBox.prototype.disable = function () {
        var $e = this.$element
        var $select = this.select()
        var $textinput = this.textinput()

        $e.attr('aria-disabled', true)
        $select.attr('aria-disabled', true)
        $textinput.addClass('disabled')

        $e.parents(formset).addClass('disabled')
    }

    SelectBox.prototype.enable = function () {
        var $e = this.$element
        var $select = this.select()
        var $textinput = this.textinput()

        $e.attr('aria-disabled', false)
        $select.attr('aria-disabled', false)
        $textinput.removeClass('disabled')

        $e.parents(formset).removeClass('disabled')
    }

    SelectBox.prototype.toggle = function () {
        if (this.isDisabled()) return
        this.isOpened() ? this.hide() : this.show()
    }

    SelectBox.prototype.getDefaults = function () {
        return SelectBox.DEFAULTS
    }

    SelectBox.prototype.getOptions = function (options) {
        options = $.extend({}, this.getDefaults(), this.$element.data(), options)
        return options
    }

    SelectBox.instanceCounter = 0
    SelectBox.prototype.getId = function () {
        return this.id = this.id || this.options.idPrefix + (++SelectBox.instanceCounter)
    }

    SelectBox.prototype.scrollwrapper = function () {
        return this.$scrollwrapper = this.$scrollwrapper || this.select().find('.form-select-js-option-scroll-wrapper')
    }

    SelectBox.prototype.optionlist = function () {
        return this.$optionlist = this.$optionlist || this.select().find('.form-select-js-options')
    }

    SelectBox.prototype.choice = function () {
        return this.$choice = this.$choice || this.select().find('.form-select-js-choice')
    }

    SelectBox.prototype.textinput = function () {
        return this.$textinput = this.$textinput || this.select().find('.form-select-js-choice-input')
    }

    SelectBox.prototype.select = function () {
        return this.$select = this.$select || $(this.options.template)
    }

    SelectBox.prototype.keyboardHandler = function (e) {
        var $this = $(this)
        var $elem = $this.next()
        var selectBox = $elem.data('tc.selectbox')
        var $textinput = selectBox.textinput()
        var $scrollWrapper = selectBox.scrollwrapper()
        var isActive = $this.hasClass('in')

        var navigationKeys = [
            13, // KeyBoard.ENTER
            27, // KeyBoard.ESCAPE
            33, // KeyBoard.PAGE_UP
            34, // KeyBoard.PAGE_DOWN
            35, // KeyBoard.END
            36, // KeyBoard.HOME
            37, // KeyBoard.LEFT
            38, // KeyBoard.UP
            39, // KeyBoard.RIGHT
            40  // KeyBoard.DOWN
        ]

        // respect tab-navigation
        if (e.keyCode === 9) // KeyBoard.TAB
            return selectBox.hide()

        if (selectBox.isDisabled()) return

        // clear textinput if it's not an 'alpha-numeric' input
        if (e.keyCode < 32) // KeyBoard.SPACE
            $textinput.val('')

        // open on SPACE, but only if it's the first input
        if (e.keyCode === 32 && $textinput.val() === '') { // KeyBoard.SPACE
            e.preventDefault()
            e.stopPropagation()
            return selectBox.toggle()
        }

        // do nothing else if it's not a valid navigation key
        if (!(new RegExp('^(' + navigationKeys.join('|') + ')$')).test(e.keyCode))
            return

        e.preventDefault()
        e.stopPropagation()
        SelectBox.suppressMouseInteractionAfterViewUpdate = true

        // toggle on ENTER
        if (e.keyCode === 13 || (e.altKey && (e.keyCode === 38 || e.keyCode === 40))) //  KeyBoard.ENTER,  KeyBoard.UP,  KeyBoard.DOWN
            return selectBox.toggle()

        // only close on ESCAPE
        if (e.keyCode === 27) // KeyBoard.ESCAPE
            return selectBox.hide()

        // now go for it ...
        var desc = 'li:not([aria-disabled="true"])'
        var $itemsEnabled = $scrollWrapper.find(desc)

        if (!$itemsEnabled.length) return

        var indexEnabled = $itemsEnabled.index($itemsEnabled.filter('.selected'))
        if (indexEnabled === -1) {
            var $items = $scrollWrapper.find('li')
            var tempIndex = $items.index($items.filter('.selected'))
            while (indexEnabled === -1 && --tempIndex >= 0)
                indexEnabled = $itemsEnabled.index($items.eq(tempIndex))

            if (indexEnabled === -1) {
                tempIndex = $items.index($items.filter('.selected'))
                var len = $items.length
                while (indexEnabled === -1 && ++tempIndex < len)
                    indexEnabled = $itemsEnabled.index($items.eq(tempIndex))
                indexEnabled -= 0.5
            } else {
                indexEnabled += 0.5
            }
        }

        if (e.keyCode === 33 || e.keyCode === 34) { // KeyBoard.PAGE_UP, KeyBoard.PAGE_DOWN

            if (selectBox.isOpened()) {
                var pageTopY = $scrollWrapper.scrollTop()
                var pageBottomY = pageTopY + $scrollWrapper.height()
                var visibleItemsNum = selectBox.getVisibleItemsNum()

                // find top most of the visible items
                var first = 0
                while (first < $itemsEnabled.length && pageTopY > $itemsEnabled[first].offsetTop) {
                    first++
                }

                // find last visible item
                var last = first
                while (last < $itemsEnabled.length && $itemsEnabled[last].offsetTop + $itemsEnabled[last].offsetHeight < pageBottomY) {
                    last++
                }

                if (e.keyCode === 33) { // KeyBoard.PAGE_UP
                    indexEnabled = Math.ceil((indexEnabled !== first) ? first : indexEnabled - visibleItemsNum)
                } else {
                    indexEnabled = (indexEnabled !== last) ? last : indexEnabled + visibleItemsNum
                }
            } else {
                indexEnabled += (e.keyCode === 33 ? -1 : 1) * 3 // 33 = KeyBoard.PAGE_UP
            }

        }
        var indexEnabledPrev = indexEnabled

        if (e.keyCode === 38) indexEnabled -= 1 // KeyBoard.UP
        if (e.keyCode === 40) indexEnabled += 1 // KeyBoard.DOWN
        if (e.keyCode === 37 && !isActive) indexEnabled -= 1 // KeyBoard.LEFT
        if (e.keyCode === 39 && !isActive) indexEnabled += 1 // KeyBoard.RIGHT
        if (e.keyCode === 36) indexEnabled = 0 // KeyBoard.HOME
        if (e.keyCode === 35) indexEnabled = $itemsEnabled.length - 1 // KeyBoard.END

        indexEnabled = Math.max(0, Math.min(indexEnabled, $itemsEnabled.length - 1))

        if (!~indexEnabled) indexEnabled = 0

        if (indexEnabled % 1) {
            if (indexEnabledPrev < indexEnabled) --indexEnabled
            indexEnabled = Math.round(indexEnabled)
        }

        selectBox.setSelectedOption($itemsEnabled.eq(indexEnabled))
    }

    SelectBox.prototype.textInputChangeHandler = function (e) {

        var $this = $(this)
        var $elem = $this.next()
        var selectBox = $elem.data('tc.selectbox')
        var $textinput = selectBox.textinput()
        var $scrollWrapper = selectBox.scrollwrapper()

        // try to find something that makes sense .... seriously

        // but first of all - clear text input value after 1000ms
        clearTimeout($.data(this, 'tiClearTimer'))
        $.data(this, 'tiClearTimer', setTimeout(function () {
            this.previousSearchText = ''
            $textinput.val('')
        }, 1000))
        var lastChar = String.fromCharCode(e.keyCode)
        var searchText = ($textinput.val().toString() + (lastChar !== this.previousSearchText ? lastChar : '')).toLowerCase()

        if (searchText === '') return

        var desc = 'li:not([aria-disabled="true"])'
        var $itemsEnabled = $(desc, $scrollWrapper)
        if (!$itemsEnabled.length) return

        var currentIndex = $itemsEnabled.index($itemsEnabled.filter('.selected'))

        var matchingIndex = -1
        if (searchText.length > 1)
            matchingIndex = selectBox.findNextOptionByText($itemsEnabled, searchText, currentIndex - 1)
        if (matchingIndex !== -1)
            return selectBox.setSelectedOption($itemsEnabled.eq(matchingIndex))

        if (lastChar && lastChar !== '') {
            matchingIndex = selectBox.findNextOptionByText($itemsEnabled, lastChar, currentIndex)
            if (matchingIndex !== -1)
                return selectBox.setSelectedOption($itemsEnabled.eq(matchingIndex))
        }
        this.previousSearchText = searchText

    }

    SelectBox.prototype.findNextOptionByText = function ($itemsEnabled, text, startIndex) {

        var needle = text.toLowerCase()
        if (needle === '')
            return -1

        var firstIndex = -1
        var nextIndex = -1

        $itemsEnabled.each(function (idx) {
            if ($(this).text().toLowerCase().indexOf(needle) === 0) {
                if (firstIndex === -1) firstIndex = idx // hit
                if (nextIndex === -1 && idx > startIndex) return nextIndex = idx
            }
        })

        return Math.max(firstIndex, nextIndex)
    }

    SelectBox.prototype.focusHandler = function (e) {
        var $selectBox = $(this).parent()
        e.type === 'focusin' ? $selectBox.addClass('focus') : $selectBox.removeClass('focus')
    }

    SelectBox.prototype.updateScrollView = function () {

        var $optionlist = this.optionlist()
        var $selected = $optionlist.find('.selected')
        if (!$selected[0]) return

        var $scrollWrapper = this.scrollwrapper()
        var scrollY = $scrollWrapper.scrollTop()
        var scrollwrapperDimensions = this.measureScrollwrapperDimensions()

        var selectionScrollY = $selected[0].offsetTop
        var selectionHeight = $selected[0].offsetHeight
        if (scrollY > selectionScrollY) {
            $scrollWrapper.scrollTop(selectionScrollY)
        } else if (scrollY + scrollwrapperDimensions.height < selectionScrollY + selectionHeight) {
            $scrollWrapper.scrollTop(selectionScrollY + selectionHeight - scrollwrapperDimensions.height)
        }
    }

    // SELECT PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.selectbox')
            var options = typeof option === 'object' && option

            if (!data) $this.data('tc.selectbox', (data = new SelectBox(this, options)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.selectbox

    $.fn.selectbox = Plugin
    $.fn.selectbox.Constructor = SelectBox


    // SELECT NO CONFLICT
    // ====================

    $.fn.selectbox.noConflict = function () {
        $.fn.selectbox = old
        return this
    }

    // APPLY TO STANDARD DROPDOWN ELEMENTS
    // ===================================

    $(document)
        .on('keydown.tc.selectbox.data-api', '.form-select-js', SelectBox.prototype.keyboardHandler)
        .on('keypress.tc.selectbox.data-api', '.form-select-js', SelectBox.prototype.textInputChangeHandler)

        .on('focusin.tc.selectbox.data-api', '.form-select-js-choice-input', SelectBox.prototype.focusHandler)
        .on('focusout.tc.selectbox.data-api', '.form-select-js-choice-input', SelectBox.prototype.focusHandler)

}(window.jQuery, window);

+function ($) {
    'use strict';

    // MODAL CLASS DEFINITION
    // ======================

    var Modal = function (element, options) {
        this.options = options
        this.$body = $(document.body)
        this.$element = $(element)
        this.$backdrop =
            this.isShown = null
        this.scrollbarWidth = 0

        if (this.options.remote) {
            this.$element
                .find('.modal-content')
                .load(this.options.remote, $.proxy(function () {
                    this.$element.trigger('loaded.tc.modal')
                }, this))
        }
    }

    Modal.VERSION = '1'

    Modal.TRANSITION_DURATION = 300
    Modal.BACKDROP_TRANSITION_DURATION = 150

    Modal.DEFAULTS = {
        backdrop: true,
        keyboard: true,
        show: true,
        template: '<div class="modal-dialog">' +
        '<div class="modal-content">' +
        '<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">x</span><span class="sr-only">Close</span></button>' +
        '</div>' +
        '</div>'
    }

    Modal.prototype.toggle = function (_relatedTarget) {
        return this.isShown ? this.hide() : this.show(_relatedTarget)
    }

    Modal.prototype.show = function (_relatedTarget) {
        var that = this
        var e = $.Event('show.tc.modal', {relatedTarget: _relatedTarget})

        this.$element.trigger(e)

        if (this.isShown || e.isDefaultPrevented())
            return

        this.isShown = true

        this.checkScrollbar()
        this.setScrollbar()
        this.$body.addClass('modal-open')

        this.escape()
        this.resize()

        this.$element.on('click.dismiss.tc.modal', '[data-dismiss="modal"]', $.proxy(this.hide, this))

        this.backdrop(function () {
            var transition = $.support.transition && that.$element.hasClass('fade')

            if (!that.$element.parent().length) {
                that.$element.appendTo(that.$body) // don't move modals dom position
            }

            that.$element
                .show()
                .scrollTop(0)

            if (that.options.backdrop)
                that.adjustBackdrop()
            that.adjustDialog()

            if (transition) {
                that.$element[0].offsetWidth // force reflow
            }

            that.$element
                .addClass('in')
                .attr('aria-hidden', false)

            that.enforceFocus()

            var e = $.Event('shown.tc.modal', {relatedTarget: _relatedTarget})

            transition ?
                that.$element.find('.modal-dialog') // wait for modal to slide in
                    .one('tcTransitionEnd', function () {
                        that.$element.trigger('focus').trigger(e)
                    })
                    .emulateTransitionEnd(Modal.TRANSITION_DURATION) :
                that.$element.trigger('focus').trigger(e)
        })
    }

    Modal.prototype.hide = function (e) {
        if (e)
            e.preventDefault()

        e = $.Event('hide.tc.modal')

        this.$element.trigger(e)

        if (!this.isShown || e.isDefaultPrevented())
            return

        this.isShown = false

        this.escape()
        this.resize()

        $(document).off('focusin.tc.modal')

        this.$element
            .removeClass('in')
            .attr('aria-hidden', true)
            .off('click.dismiss.tc.modal')

        $.support.transition && this.$element.hasClass('fade') ?
            this.$element
                .one('tcTransitionEnd', $.proxy(this.hideModal, this))
                .emulateTransitionEnd(Modal.TRANSITION_DURATION) :
            this.hideModal()
    }

    Modal.prototype.enforceFocus = function () {
        $(document)
            .off('focusin.tc.modal') // guard against infinite focus loop
            .on('focusin.tc.modal', $.proxy(function (e) {
                if (this.$element[0] !== e.target && !this.$element.has(e.target).length) {
                    this.$element.trigger('focus')
                }
            }, this))
    }

    Modal.prototype.escape = function () {
        if (this.isShown && this.options.keyboard) {
            this.$element.on('keydown.dismiss.tc.modal', $.proxy(function (e) {
                if (e.which == 8) {
                    if (($(e.target || e.srcElement).prop('tagName') != 'TEXTAREA') && ($(e.target || e.srcElement).prop('tagName') != 'INPUT')) {
                        e.preventDefault()
                        e.stopImmediatePropagation()
                    }
                }
                e.which == 27 && this.hide()
            }, this))
        } else if (!this.isShown) {
            this.$element.off('keydown.dismiss.tc.modal')
        }
    }

    Modal.prototype.resize = function () {
        if (this.isShown) {
            $(window).on('resize.tc.modal', $.proxy(this.handleUpdate, this))
        } else {
            $(window).off('resize.tc.modal')
        }
    }

    Modal.prototype.hideModal = function () {
        var that = this
        this.$element.hide()
        this.backdrop(function () {
            that.$body.removeClass('modal-open')
            that.resetAdjustments()
            that.resetScrollbar()
            that.$element.trigger('hidden.tc.modal')
        })
    }

    Modal.prototype.removeBackdrop = function () {
        this.$backdrop && this.$backdrop.remove()
        this.$backdrop = null
    }

    Modal.prototype.backdrop = function (callback) {
        var that = this
        var animate = this.$element.hasClass('fade') ? 'fade' : ''

        if (this.isShown && this.options.backdrop) {
            var doAnimate = $.support.transition && animate

            this.$backdrop = $('<div class="modal-backdrop ' + animate + '" />')
                .prependTo(this.$element)
                .on('click.dismiss.tc.modal', $.proxy(function (e) {
                    if (e.target !== e.currentTarget)
                        return
                    this.options.backdrop == 'static'
                        ? this.$element[0].focus.call(this.$element[0])
                        : this.hide.call(this)
                }, this))

            if (doAnimate)
                this.$backdrop[0].offsetWidth // force reflow

            this.$backdrop.addClass('in')

            if (!callback)
                return

            doAnimate ?
                this.$backdrop
                    .one('tcTransitionEnd', callback)
                    .emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) :
                callback()

        } else if (!this.isShown && this.$backdrop) {
            this.$backdrop.removeClass('in')

            var callbackRemove = function () {
                that.removeBackdrop()
                callback && callback()
            }
            $.support.transition && this.$element.hasClass('fade') ?
                this.$backdrop
                    .one('tcTransitionEnd', callbackRemove)
                    .emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) :
                callbackRemove()

        } else if (callback) {
            callback()
        }
    }

    // these following methods are used to handle overflowing modals

    Modal.prototype.handleUpdate = function () {
        if (this.options.backdrop)
            this.adjustBackdrop()
        this.adjustDialog()
    }

    Modal.prototype.adjustBackdrop = function () {
        this.$backdrop
            .css('height', 0)
            .css('height', this.$element[0].scrollHeight)
    }

    Modal.prototype.adjustDialog = function () {
        var modalIsOverflowing = this.$element[0].scrollHeight > document.documentElement.clientHeight

        this.$element.css({
            paddingLeft: !this.bodyIsOverflowing && modalIsOverflowing ? this.scrollbarWidth : '',
            paddingRight: this.bodyIsOverflowing && !modalIsOverflowing ? this.scrollbarWidth : ''
        })
    }

    Modal.prototype.resetAdjustments = function () {
        this.$element.css({
            paddingLeft: '',
            paddingRight: ''
        })
    }

    Modal.prototype.checkScrollbar = function () {
        this.bodyIsOverflowing = document.body.scrollHeight > document.documentElement.clientHeight
        this.scrollbarWidth = this.measureScrollbar()
    }

    Modal.prototype.setScrollbar = function () {
        var bodyPad = parseInt((this.$body.css('padding-right') || 0), 10)
        if (this.bodyIsOverflowing)
            this.$body.css('padding-right', bodyPad + this.scrollbarWidth)
    }

    Modal.prototype.resetScrollbar = function () {
        this.$body.css('padding-right', '')
    }

    Modal.prototype.measureScrollbar = function () { // thx walsh
        var scrollDiv = document.createElement('div')
        scrollDiv.className = 'modal-scrollbar-measure'
        this.$body.append(scrollDiv)
        var scrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth
        this.$body[0].removeChild(scrollDiv)
        return scrollbarWidth
    }


    // MODAL PLUGIN DEFINITION
    // =======================

    function Plugin(option, _relatedTarget) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.modal')
            var options = $.extend({}, Modal.DEFAULTS, $this.data(), typeof option === 'object' && option)

            if ($this.attr('tabindex') === undefined)
                $this.attr('tabindex', -1);

            if (!data && options.template && $this.find('.modal-content').length === 0) {
                var inner = $this.html();
                $this.html(options.template).find('.modal-content').append(inner);
            }

            if (!data)
                $this.data('tc.modal', (data = new Modal(this, options)))
            if (typeof option === 'string')
                data[option](_relatedTarget)
            else if (options.show)
                data.show(_relatedTarget)
        })
    }

    var old = $.fn.modal

    $.fn.modal = Plugin
    $.fn.modal.Constructor = Modal


    // MODAL NO CONFLICT
    // =================

    $.fn.modal.noConflict = function () {
        $.fn.modal = old
        return this
    }


    // MODAL DATA-API
    // ==============

    $(document).on('click.tc.modal.data-api', '[data-toggle="modal"]', function (e) {
        var $this = $(this)
        var href = $this.attr('href')
        var $target = $($this.attr('data-target') || (href && href.replace(/.*(?=#[^\s]+$)/, ''))) // strip for ie7
        var option = $target.data('tc.modal') ? 'toggle' : $.extend({remote: !/#/.test(href) && href}, $target.data(), $this.data())

        if ($this.is('a'))
            e.preventDefault()

        $target.one('show.tc.modal', function (showEvent) {
            if (showEvent.isDefaultPrevented())
                return // only register focus restorer if modal will actually get shown
            $target.one('hidden.tc.modal', function () {
                $this.is(':visible') && $this.trigger('focus')
            })
        })
        Plugin.call($target, option, this)
    })

}(jQuery);

+function ($) {
    'use strict';


    var dismiss = '[data-dismiss="notification"]'
    var Notification = function (el) {
        $(el).on('click', dismiss, this.close)
    }

    Notification.VERSION = '1.0.0'

    Notification.TRANSITION_DURATION = 150

    Notification.prototype.close = function (e) {
        var $this = $(this)
        var selector = $this.attr('data-target')

        if (!selector) {
            selector = $this.attr('href')
            selector = selector && selector.replace(/.*(?=#[^\s]*$)/, '') // strip for ie7
        }

        var $parent = $(selector)

        if (e) e.preventDefault()

        if (!$parent.length) {
            $parent = $this.closest('.notification')
        }

        $parent.trigger(e = $.Event('close.tc.notification'))

        if (e.isDefaultPrevented()) return

        $parent.removeClass('in')

        function removeElement() {
            // detach from parent, fire event then clean up data
            $parent.detach().trigger('closed.tc.notification').remove()
        }

        $.support.transition && $parent.hasClass('fade') ?
            $parent
                .one('tcTransitionEnd', removeElement)
                .emulateTransitionEnd(Notification.TRANSITION_DURATION) :
            removeElement()
    }


    // NOTIFICATION PLUGIN DEFINITION
    // =======================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.notification')

            if (!data) $this.data('tc.notification', (data = new Notification(this)))
            if (typeof option === 'string') data[option].call($this)
        })
    }

    var old = $.fn.notification

    $.fn.notification = Plugin
    $.fn.notification.Constructor = Notification


    // NOTIFICATION NO CONFLICT
    // =================

    $.fn.notification.noConflict = function () {
        $.fn.notification = old
        return this
    }


    // NOTIFICATION DATA-API
    // ==============

    $(document).on('click.tc.notification.data-api', dismiss, Notification.prototype.close)

}(window.jQuery);

+function ($) {
    'use strict';


    var BrandNavHead = function (element, options) {
        this.options = $.extend({}, BrandNavHead.DEFAULTS, options)

        this.$target = $(this.options.target)
            .on('show.tc.brandnav', $.proxy(this.update, this))
            .on('hide.tc.brandnav', $.proxy(this.update, this))

        this.$element = $(element)

        this.$label = this.$element.find('.brandnav-label')
        this.$controlUp = this.$element.find('.brandnav-control-up')

        this.storeLabel()
    }

    BrandNavHead.VERSION = '1.0.0'

    BrandNavHead.TRANSITION_DURATION = 300

    BrandNavHead.DEFAULTS = {
        target: window
    }

    BrandNavHead.prototype.storeLabel = function (e) {
        this.$label.data('root-label', this.$label.text())
    }

    BrandNavHead.prototype.update = function (e) {
        this.updateLabel(e)
        this.updateControls(e)
    }

    BrandNavHead.prototype.updateLabel = function (e) {
        var text = e.target.siblings('[data-open="brandnav"]').text()
        this.$label.text(text || this.$label.data('root-label'))
    }

    BrandNavHead.prototype.updateControls = function (e) {
        this.$controlUp.toggleClass('in', !!e.target.parents('.brandnav').length)
    }

    // MENU PLUGIN DEFINITION
    // =======================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.brandnavhead')

            if (!data) $this.data('tc.brandnavhead', (data = new BrandNavHead(this)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.brandnavhead

    $.fn.brandnavhead = Plugin
    $.fn.brandnavhead.Constructor = BrandNavHead


    // BRANDNAVHEAD NO CONFLICT
    // =================

    $.fn.brandnavhead.noConflict = function () {
        $.fn.brandnavhead = old
        return this
    }

    // BRANDNAVHEAD DATA-API
    // ==============

    $(document).on('click.tc.brandnavhead.data-api', '[data-spy="brandnav"]', function (e) {
        e.preventDefault() // only accept click events on button inside
    })

    $(window).on('load.tc.brandnavhead.data-api', function () {
        $('[data-spy="brandnav"]').each(function () {
            var $spy = $(this)
            Plugin.call($spy, $spy.data())
        })
    })

}(window.jQuery);

+function ($) {
    'use strict';

    var navOpen = '[data-open="brandnav"]'
    var navClose = '[data-close="brandnav"]'
    var navCancel = '[data-cancel="brandnav"]'
    var BrandNav = function (element) {
        this.$element = $(element)
        this.$parent = this.$element.parent()
        this.$parentnav = this.$element.parents('.brandnav').first()

        this.$main = getMain(this.$element)
        this.$body = $('body')
    }

    BrandNav.VERSION = '1.0.0'

    BrandNav.TRANSITION_DURATION = 300

    BrandNav.prototype.show = function () {

        var that = this

        var showEvent = $.Event('show.tc.brandnav', {
            target: this.$element,
            relatedTarget: this.$parentnav
        })

        this.$main.trigger(showEvent)
        if (showEvent.isDefaultPrevented()) return

        this.$body.css('overflow-x', 'hidden')
        this.$parent.siblings().removeClass('active')

        this.$parent.addClass('active')
        this.$parentnav.addClass('has-active')

        // activate up the road
        this.$element.parents('.brandnav').each(function () {
            $(this).parent().addClass('active')
        })

        var shownEvent = $.Event('shown.tc.brandnav', {
            target: this.$element,
            relatedTarget: this.$parentnav
        })

        if ($.support.transition) {
            this.$parentnav
                .one('tcTransitionEnd', function () {
                    setTimeout(function () {
                        that.$main.trigger(shownEvent)
                        that.$body.css('overflow-x', '')
                    })
                }).emulateTransitionEnd(BrandNav.TRANSITION_DURATION)
        } else {
            that.$body.css('overflow-x', '')
        }
    }

    BrandNav.prototype.hide = function () {

        var that = this

        var hideEvent = $.Event('hide.tc.brandnav', {
            target: this.$parentnav,
            relatedTarget: this.$element
        })

        this.$main.trigger(hideEvent)
        if (hideEvent.isDefaultPrevented()) return

        this.$body.css('overflow-x', 'hidden')
        this.$parentnav.removeClass('has-active')

        var hiddenEvent = $.Event('hidden.tc.brandnav', {
            target: this.$parentnav,
            relatedTarget: this.$element
        })

        if ($.support.transition) {
            this.$parentnav
                .one('tcTransitionEnd', function () {
                    that.$parent.removeClass('active')
                    that.$body.css('overflow-x', '')
                    setTimeout(function () {
                        that.$main.trigger(hiddenEvent)
                    })
                }).emulateTransitionEnd(BrandNav.TRANSITION_DURATION)
        } else {
            this.$parent.removeClass('active')
            that.$body.css('overflow-x', '')
        }
    }

    function getSiblingNav($this) {
        var selector = $this.attr('data-target')

        if (!selector) {
            selector = $this.attr('href')
            selector = selector && /#[A-Za-z]/.test(selector) && selector.replace(/.*(?=#[^\s]*$)/, '') // strip for ie7
        }

        var $parent = selector && $(selector)

        return $parent && $parent.length ? $parent : $this.siblings('.brandnav')
    }

    function getMain($this) {
        return $this.closest('.brandnav-lvl-1').last()
    }

    function getCurrentFromTarget($target) {
        return $target.find('.active > .brandnav').last()
    }

    // BRANDNAV PLUGIN DEFINITION
    // =======================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.brandnav')

            if (!data) $this.data('tc.brandnav', (data = new BrandNav(this)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.brandnav

    $.fn.brandnav = Plugin
    $.fn.brandnav.Constructor = BrandNav


    // NOTIFICATION NO CONFLICT
    // =================

    $.fn.brandnav.noConflict = function () {
        $.fn.brandnav = old
        return this
    }

    // NOTIFICATION DATA-API
    // ==============

    var closeCurrent = function (e) {
        e.preventDefault()
        var $target = getSiblingNav($(this))
        var $activeNav = getCurrentFromTarget($target)
        Plugin.call($activeNav, 'hide')
    }

    function clearMenus(e) {
        if (e && e.which === 3) return

        $(navOpen).each(function () {
            var $trigger = $(this)
            var $target = getSiblingNav($trigger)
            var $main = getMain($target)
            var $parent = $main.parent()

            if (!$parent.hasClass('active')) return

            if (e && e.type == 'click' && $.contains($main[0], e.target)) return

            if (e.isDefaultPrevented()) return

            $parent.find('.brandnav').each(function () {
                var $nav = $(this)
                $nav.removeClass('has-active')
                $nav.parent().removeClass('active')
            })
        })
    }

    var clickHandler = function (e) {
        e.preventDefault()
        var $target = getSiblingNav($(this))
        Plugin.call($target, 'show')
    }

    $(document)
        .on('click.tc.brandnav.data-api', clearMenus)
        .on('click.tc.brandnav.data-api', navOpen, clickHandler)
        .on('click.tc.brandnav.data-api', navClose, closeCurrent)
        .on('click.tc.brandnav.data-api', navCancel, clearMenus)

}(window.jQuery);

+function ($) {
    'use strict';

    // COLLAPSE PUBLIC CLASS DEFINITION
    // ================================

    var Collapse = function (element, options) {
        this.$element = $(element)
        this.options = $.extend({}, Collapse.DEFAULTS, options)
        this.$trigger = $('[data-toggle="collapse"][href="#' + element.id + '"],' +
            '[data-toggle="collapse"][data-target="#' + element.id + '"]')
        this.transitioning = null

        if (this.options.parent) {
            this.$parent = this.getParent()
        } else {
            this.addAriaAndCollapsedClass(this.$element, this.$trigger)
        }

        if (this.options.toggle) this.toggle()
    }

    Collapse.VERSION = '1.1.0'

    Collapse.TRANSITION_DURATION = 150

    Collapse.DEFAULTS = {
        toggle: true
    }

    Collapse.prototype.dimension = function () {
        var hasWidth = this.$element.hasClass('width')
        return hasWidth ? 'width' : 'height'
    }

    Collapse.prototype.show = function () {
        if (this.transitioning || this.$element.hasClass('in')) return

        var activesData
        var actives = this.$parent && this.$parent.children('.panel').children('.in, .collapsing')

        if (actives && actives.length) {
            activesData = actives.data('tc.collapse')
            if (activesData && activesData.transitioning) return
        }

        var startEvent = $.Event('show.tc.collapse')
        this.$element.trigger(startEvent)
        if (startEvent.isDefaultPrevented()) return

        if (actives && actives.length) {
            Plugin.call(actives, 'hide')
            activesData || actives.data('tc.collapse', null)
        }

        var dimension = this.dimension()

        this.$element
            .removeClass('collapse')
            .addClass('collapsing')[dimension](0)
            .attr('aria-expanded', true)

        this.$trigger
            .removeClass('collapsed')
            .attr('aria-expanded', true)

        this.transitioning = 1

        var complete = function () {
            this.$element
                .removeClass('collapsing')
                .addClass('collapse in')[dimension]('')
            this.transitioning = 0
            this.$element
                .trigger('shown.tc.collapse')
        }

        if (!$.support.transition) return complete.call(this)

        var scrollSize = $.camelCase(['scroll', dimension].join('-'))

        this.$element
            .one('tcTransitionEnd', $.proxy(complete, this))
            .emulateTransitionEnd(Collapse.TRANSITION_DURATION)[dimension](this.$element[0][scrollSize])
    }

    Collapse.prototype.hide = function () {
        if (this.transitioning || !this.$element.hasClass('in')) return

        var startEvent = $.Event('hide.tc.collapse')
        this.$element.trigger(startEvent)
        if (startEvent.isDefaultPrevented()) return

        var dimension = this.dimension()

        this.$element[dimension](this.$element[dimension]())[0].offsetHeight

        this.$element
            .addClass('collapsing')
            .removeClass('collapse in')
            .attr('aria-expanded', false)

        this.$trigger
            .addClass('collapsed')
            .attr('aria-expanded', false)

        this.transitioning = 1

        var complete = function () {
            this.transitioning = 0
            this.$element
                .removeClass('collapsing')
                .addClass('collapse')
                .trigger('hidden.tc.collapse')
        }

        if (!$.support.transition) return complete.call(this)

        this.$element
            [dimension](0)
            .one('tcTransitionEnd', $.proxy(complete, this))
            .emulateTransitionEnd(Collapse.TRANSITION_DURATION)
    }

    Collapse.prototype.toggle = function () {
        this[this.$element.hasClass('in') ? 'hide' : 'show']()
    }

    Collapse.prototype.getParent = function () {
        return $(this.options.parent)
            .find('[data-toggle="collapse"][data-parent="' + this.options.parent + '"]')
            .each($.proxy(function (i, element) {
                var $element = $(element)
                this.addAriaAndCollapsedClass(getTargetFromTrigger($element), $element)
            }, this))
            .end()
    }

    Collapse.prototype.addAriaAndCollapsedClass = function ($element, $trigger) {
        var isOpen = $element.hasClass('in')

        $element.attr('aria-expanded', isOpen)
        $trigger
            .toggleClass('collapsed', !isOpen)
            .attr('aria-expanded', isOpen)
    }

    function getTargetFromTrigger($trigger) {
        var href
        var target = $trigger.attr('data-target')
            || (href = $trigger.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') // strip for ie7

        return $(target)
    }


    // COLLAPSE PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.collapse')
            var options = $.extend({}, Collapse.DEFAULTS, $this.data(), typeof option === 'object' && option)

            if (!data && options.toggle && /show|hide/.test(option)) options.toggle = false
            if (!data) $this.data('tc.collapse', (data = new Collapse(this, options)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.collapse

    $.fn.collapse = Plugin
    $.fn.collapse.Constructor = Collapse


    // COLLAPSE NO CONFLICT
    // ====================

    $.fn.collapse.noConflict = function () {
        $.fn.collapse = old
        return this
    }


    // COLLAPSE DATA-API
    // =================

    $(document).on('click.tc.collapse.data-api', '[data-toggle="collapse"]', function (e) {
        var $this = $(this)

        if (!$this.attr('data-target')) e.preventDefault()

        var $target = getTargetFromTrigger($this)
        var data = $target.data('tc.collapse')
        var option = data ? 'toggle' : $this.data()

        Plugin.call($target, option)
    })

}(jQuery);

+function ($) {
    'use strict';

    var Search = function (element, options) {
        this.$element = $(element)
        this.options = $.extend({}, Search.DEFAULTS, options)
        this.$trigger = $('[data-toggle="search"][href="#' + element.id + '"],' +
            '[data-toggle="search"][data-target="#' + element.id + '"]')
        this.transitioning = null

        if (this.options.toggle) this.toggle()
    }

    Search.DEFAULTS = {
        toggle: true,
        keyboard: true,
        onBlur: false
    }

    Search.TRANSITION_DURATION = 250

    Search.prototype.show = function () {
        if (this.transitioning || this.$element.hasClass('in')) return

        var startEvent = $.Event('show.tc.search')
        this.$element.trigger(startEvent)
        if (startEvent.isDefaultPrevented()) return

        this.$element
            .removeClass('search')
            .attr('aria-expanded', true)

        this.$trigger
            .attr('aria-expanded', true)
            .blur()

        this.transitioning = 1

        var complete = function () {
            this.$element
                .addClass('search in')
            this.transitioning = 0
            this.enforceFocus()
            this.escape()
            this.$element
                .trigger('shown.tc.search')

            if (this.options.onBlur === 'dismiss') {
                this.$element.on('blur.dismiss.tc.search', 'input', $.proxy(this.hide, this))
            }
        }

        if (!$.support.transition) return complete.call(this)

        this.$element
            .one('tcTransitionEnd', $.proxy(complete, this))
            .emulateTransitionEnd(Search.TRANSITION_DURATION)
    }

    Search.prototype.hide = function () {
        if (this.transitioning || !this.$element.hasClass('in')) return

        var startEvent = $.Event('hide.tc.search')
        this.$element.trigger(startEvent)

        if (startEvent.isDefaultPrevented()) return

        this.$element
            .removeClass('search in')
            .attr('aria-expanded', false)

        this.$trigger
            .attr('aria-expanded', false)

        this.transitioning = 1

        var complete = function () {
            this.transitioning = 0
            this.$element
                .addClass('search')
                .trigger('hidden.tc.search')

            this.$element.off('blur.dismiss.tc.search', 'input')
        }

        if (!$.support.transition) return complete.call(this)

        this.$element
            .one('tcTransitionEnd', $.proxy(complete, this))
            .emulateTransitionEnd(Search.TRANSITION_DURATION)
    }

    Search.prototype.toggle = function () {
        this[this.$element.hasClass('in') ? 'hide' : 'show']()
    }

    Search.prototype.enforceFocus = function () {
        this.$element.find('input').trigger('focus')
    }

    Search.prototype.escape = function () {
        if (this.$element.hasClass('in') && this.options.keyboard) {
            this.$element.on('keydown.dismiss.tc.search', $.proxy(function (e) {
                e.which == 27 && this.hide()
            }, this))
        } else if (!this.$element.hasClass('in')) {
            this.$element.off('keydown.dismiss.tc.search')
        }
    }

    function getTargetFromTrigger($trigger) {
        var href
        var target = $trigger.attr('data-target')
            || (href = $trigger.attr('href')) && href.replace(/.*(?=#[^\s]+$)/, '') // strip for ie7

        return $(target)
    }

    // SEARCH PLUGIN DEFINITION
    // =====================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.search')
            var options = $.extend({}, Search.DEFAULTS, $this.data(), typeof option === 'object' && option)

            if (!data && options.toggle && /show|hide/.test(option)) {
                options.toggle = false
            }

            if (!data) $this.data('tc.search', (data = new Search(this, options)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.search

    $.fn.search = Plugin
    $.fn.search.Constructor = Search


    // SEARCH NO CONFLICT
    // ===============

    $.fn.search.noConflict = function () {
        $.fn.search = old
        return this
    }


    // SEARCH DATA-API
    // ============

    $(document)
        .on('click.tc.search.data-api', '[data-toggle="search"]', function (e) {
            var $this = $(this)

            if (!$this.attr('data-target')) e.preventDefault()

            var $target = getTargetFromTrigger($this)
            var data = $target.data('tc.search')
            var option = data ? 'toggle' : $this.data()

            Plugin.call($target, option)
        })

}(jQuery);

/*
 * qTip2 - Pretty powerful tooltips - v2.2.1
 * http://qtip2.com
 *
 * Copyright (c) 2014
 * Released under the MIT licenses
 * http://jquery.org/license
 *
 * Date: Mon Sep 8 2014 10:07 EDT-0400
 * Plugins: tips
 * Styles: core
 */
/* global window: false, jQuery: false, console: false, define: false */

/* Cache window, document, undefined */
(function (window, document, undefined) {

// Uses AMD or browser globals to create a jQuery plugin.
    (function (factory) {
        "use strict";
        if (typeof define === 'function' && define.amd) {
            define(['jquery'], factory);
        }
        else if (jQuery && !jQuery.fn.qtip) {
            factory(jQuery);
        }
    }
    (function ($) {
        "use strict"; // Enable ECMAScript "strict" operation for this function. See more: http://ejohn.org/blog/ecmascript-5-strict-mode-json-and-more/
        ;// Munge the primitives - Paul Irish tip
        var TRUE = true,
            FALSE = false,
            NULL = null,

// Common variables
            X = 'x', Y = 'y',
            WIDTH = 'width',
            HEIGHT = 'height',

// Positioning sides
            TOP = 'top',
            LEFT = 'left',
            BOTTOM = 'bottom',
            RIGHT = 'right',
            CENTER = 'center',

// Position adjustment types
            FLIP = 'flip',
            FLIPINVERT = 'flipinvert',
            SHIFT = 'shift',

// Shortcut vars
            QTIP, PROTOTYPE, CORNER, CHECKS,
            PLUGINS = {},
            NAMESPACE = 'qtip',
            ATTR_HAS = 'data-hasqtip',
            ATTR_ID = 'data-qtip-id',
            WIDGET = ['ui-widget', 'ui-tooltip'],
            SELECTOR = '.' + NAMESPACE,
            INACTIVE_EVENTS = 'click dblclick mousedown mouseup mousemove mouseleave mouseenter'.split(' '),

            CLASS_FIXED = NAMESPACE + '-fixed',
            CLASS_DEFAULT = NAMESPACE + '-default',
            CLASS_FOCUS = NAMESPACE + '-focus',
            CLASS_HOVER = NAMESPACE + '-hover',
            CLASS_DISABLED = NAMESPACE + '-disabled',

            replaceSuffix = '_replacedByqTip',
            oldtitle = 'oldtitle',
            trackingBound,

// Browser detection
            BROWSER = {
                /*
	 * IE version detection
	 *
	 * Adapted from: http://ajaxian.com/archives/attack-of-the-ie-conditional-comment
	 * Credit to James Padolsey for the original implemntation!
	 */
                ie: (function () {
                    for (
                        var v = 4, i = document.createElement("div");
                        (i.innerHTML = "<!--[if gt IE " + v + "]><i></i><![endif]-->") && i.getElementsByTagName("i")[0];
                        v += 1
                    ) {
                    }
                    return v > 4 ? v : NaN;
                }()),

                /*
	 * iOS version detection
	 */
                iOS: parseFloat(
                    ('' + (/CPU.*OS ([0-9_]{1,5})|(CPU like).*AppleWebKit.*Mobile/i.exec(navigator.userAgent) || [0, ''])[1])
                        .replace('undefined', '3_2').replace('_', '.').replace('_', '')
                ) || FALSE
            };
        ;

        function QTip(target, options, id, attr) {
            // Elements and ID
            this.id = id;
            this.target = target;
            this.tooltip = NULL;
            this.elements = {target: target};

            // Internal constructs
            this._id = NAMESPACE + '-' + id;
            this.timers = {img: {}};
            this.options = options;
            this.plugins = {};

            // Cache object
            this.cache = {
                event: {},
                target: $(),
                disabled: FALSE,
                attr: attr,
                onTooltip: FALSE,
                lastClass: ''
            };

            // Set the initial flags
            this.rendered = this.destroyed = this.disabled = this.waiting =
                this.hiddenDuringWait = this.positioning = this.triggering = FALSE;
        }

        PROTOTYPE = QTip.prototype;

        PROTOTYPE._when = function (deferreds) {
            return $.when.apply($, deferreds);
        };

        PROTOTYPE.render = function (show) {
            if (this.rendered || this.destroyed) {
                return this;
            } // If tooltip has already been rendered, exit

            var self = this,
                options = this.options,
                cache = this.cache,
                elements = this.elements,
                text = options.content.text,
                title = options.content.title,
                button = options.content.button,
                posOptions = options.position,
                namespace = '.' + this._id + ' ',
                deferreds = [],
                tooltip;

            // Add ARIA attributes to target
            $.attr(this.target[0], 'aria-describedby', this._id);

            // Create public position object that tracks current position corners
            cache.posClass = this._createPosClass(
                (this.position = {my: posOptions.my, at: posOptions.at}).my
            );

            // Create tooltip element
            this.tooltip = elements.tooltip = tooltip = $('<div/>', {
                'id': this._id,
                'class': [NAMESPACE, CLASS_DEFAULT, options.style.classes, cache.posClass].join(' '),
                'width': options.style.width || '',
                'height': options.style.height || '',
                'tracking': posOptions.target === 'mouse' && posOptions.adjust.mouse,

                /* ARIA specific attributes */
                'role': 'alert',
                'aria-live': 'polite',
                'aria-atomic': FALSE,
                'aria-describedby': this._id + '-content',
                'aria-hidden': TRUE
            })
                .toggleClass(CLASS_DISABLED, this.disabled)
                .attr(ATTR_ID, this.id)
                .data(NAMESPACE, this)
                .appendTo(posOptions.container)
                .append(
                    // Create content element
                    elements.content = $('<div />', {
                        'class': NAMESPACE + '-content',
                        'id': this._id + '-content',
                        'aria-atomic': TRUE
                    })
                );

            // Set rendered flag and prevent redundant reposition calls for now
            this.rendered = -1;
            this.positioning = TRUE;

            // Create title...
            if (title) {
                this._createTitle();

                // Update title only if its not a callback (called in toggle if so)
                if (!$.isFunction(title)) {
                    deferreds.push(this._updateTitle(title, FALSE));
                }
            }

            // Create button
            if (button) {
                this._createButton();
            }

            // Set proper rendered flag and update content if not a callback function (called in toggle)
            if (!$.isFunction(text)) {
                deferreds.push(this._updateContent(text, FALSE));
            }
            this.rendered = TRUE;

            // Setup widget classes
            this._setWidget();

            // Initialize 'render' plugins
            $.each(PLUGINS, function (name) {
                var instance;
                if (this.initialize === 'render' && (instance = this(self))) {
                    self.plugins[name] = instance;
                }
            });

            // Unassign initial events and assign proper events
            this._unassignEvents();
            this._assignEvents();

            // When deferreds have completed
            this._when(deferreds).then(function () {
                // tooltiprender event
                self._trigger('render');

                // Reset flags
                self.positioning = FALSE;

                // Show tooltip if not hidden during wait period
                if (!self.hiddenDuringWait && (options.show.ready || show)) {
                    self.toggle(TRUE, cache.event, FALSE);
                }
                self.hiddenDuringWait = FALSE;
            });

            // Expose API
            QTIP.api[this.id] = this;

            return this;
        };

        PROTOTYPE.destroy = function (immediate) {
            // Set flag the signify destroy is taking place to plugins
            // and ensure it only gets destroyed once!
            if (this.destroyed) {
                return this.target;
            }

            function process() {
                if (this.destroyed) {
                    return;
                }
                this.destroyed = TRUE;

                var target = this.target,
                    title = target.attr(oldtitle),
                    timer;

                // Destroy tooltip if rendered
                if (this.rendered) {
                    this.tooltip.stop(1, 0).find('*').remove().end().remove();
                }

                // Destroy all plugins
                $.each(this.plugins, function (name) {
                    this.destroy && this.destroy();
                });

                // Clear timers
                for (timer in this.timers) {
                    clearTimeout(this.timers[timer]);
                }

                // Remove api object and ARIA attributes
                target.removeData(NAMESPACE)
                    .removeAttr(ATTR_ID)
                    .removeAttr(ATTR_HAS)
                    .removeAttr('aria-describedby');

                // Reset old title attribute if removed
                if (this.options.suppress && title) {
                    target.attr('title', title).removeAttr(oldtitle);
                }

                // Remove qTip events associated with this API
                this._unassignEvents();

                // Remove ID from used id objects, and delete object references
                // for better garbage collection and leak protection
                this.options = this.elements = this.cache = this.timers =
                    this.plugins = this.mouse = NULL;

                // Delete epoxsed API object
                delete QTIP.api[this.id];
            }

            // If an immediate destory is needed
            if ((immediate !== TRUE || this.triggering === 'hide') && this.rendered) {
                this.tooltip.one('tooltiphidden', $.proxy(process, this));
                !this.triggering && this.hide();
            }

            // If we're not in the process of hiding... process
            else {
                process.call(this);
            }

            return this.target;
        };
        ;

        function invalidOpt(a) {
            return a === NULL || $.type(a) !== 'object';
        }

        function invalidContent(c) {
            return !($.isFunction(c) || (c && c.attr) || c.length || ($.type(c) === 'object' && (c.jquery || c.then)));
        }

// Option object sanitizer
        function sanitizeOptions(opts) {
            var content, text, ajax, once;

            if (invalidOpt(opts)) {
                return FALSE;
            }

            if (invalidOpt(opts.metadata)) {
                opts.metadata = {type: opts.metadata};
            }

            if ('content' in opts) {
                content = opts.content;

                if (invalidOpt(content) || content.jquery || content.done) {
                    content = opts.content = {
                        text: (text = invalidContent(content) ? FALSE : content)
                    };
                }
                else {
                    text = content.text;
                }

                // DEPRECATED - Old content.ajax plugin functionality
                // Converts it into the proper Deferred syntax
                if ('ajax' in content) {
                    ajax = content.ajax;
                    once = ajax && ajax.once !== FALSE;
                    delete content.ajax;

                    content.text = function (event, api) {
                        var loading = text || $(this).attr(api.options.content.attr) || 'Loading...',

                            deferred = $.ajax(
                                $.extend({}, ajax, {context: api})
                            )
                                .then(ajax.success, NULL, ajax.error)
                                .then(function (content) {
                                        if (content && once) {
                                            api.set('content.text', content);
                                        }
                                        return content;
                                    },
                                    function (xhr, status, error) {
                                        if (api.destroyed || xhr.status === 0) {
                                            return;
                                        }
                                        api.set('content.text', status + ': ' + error);
                                    });

                        return !once ? (api.set('content.text', loading), deferred) : loading;
                    };
                }

                if ('title' in content) {
                    if ($.isPlainObject(content.title)) {
                        content.button = content.title.button;
                        content.title = content.title.text;
                    }

                    if (invalidContent(content.title || FALSE)) {
                        content.title = FALSE;
                    }
                }
            }

            if ('position' in opts && invalidOpt(opts.position)) {
                opts.position = {my: opts.position, at: opts.position};
            }

            if ('show' in opts && invalidOpt(opts.show)) {
                opts.show = opts.show.jquery ? {target: opts.show} :
                    opts.show === TRUE ? {ready: TRUE} : {event: opts.show};
            }

            if ('hide' in opts && invalidOpt(opts.hide)) {
                opts.hide = opts.hide.jquery ? {target: opts.hide} : {event: opts.hide};
            }

            if ('style' in opts && invalidOpt(opts.style)) {
                opts.style = {classes: opts.style};
            }

            // Sanitize plugin options
            $.each(PLUGINS, function () {
                this.sanitize && this.sanitize(opts);
            });

            return opts;
        }

// Setup builtin .set() option checks
        CHECKS = PROTOTYPE.checks = {
            builtin: {
                // Core checks
                '^id$': function (obj, o, v, prev) {
                    var id = v === TRUE ? QTIP.nextid : v,
                        new_id = NAMESPACE + '-' + id;

                    if (id !== FALSE && id.length > 0 && !$('#' + new_id).length) {
                        this._id = new_id;

                        if (this.rendered) {
                            this.tooltip[0].id = this._id;
                            this.elements.content[0].id = this._id + '-content';
                            this.elements.title[0].id = this._id + '-title';
                        }
                    }
                    else {
                        obj[o] = prev;
                    }
                },
                '^prerender': function (obj, o, v) {
                    v && !this.rendered && this.render(this.options.show.ready);
                },

                // Content checks
                '^content.text$': function (obj, o, v) {
                    this._updateContent(v);
                },
                '^content.attr$': function (obj, o, v, prev) {
                    if (this.options.content.text === this.target.attr(prev)) {
                        this._updateContent(this.target.attr(v));
                    }
                },
                '^content.title$': function (obj, o, v) {
                    // Remove title if content is null
                    if (!v) {
                        return this._removeTitle();
                    }

                    // If title isn't already created, create it now and update
                    v && !this.elements.title && this._createTitle();
                    this._updateTitle(v);
                },
                '^content.button$': function (obj, o, v) {
                    this._updateButton(v);
                },
                '^content.title.(text|button)$': function (obj, o, v) {
                    this.set('content.' + o, v); // Backwards title.text/button compat
                },

                // Position checks
                '^position.(my|at)$': function (obj, o, v) {
                    typeof v === 'string' && (this.position[o] = obj[o] = new CORNER(v, o === 'at'));
                },
                '^position.container$': function (obj, o, v) {
                    this.rendered && this.tooltip.appendTo(v);
                },

                // Show checks
                '^show.ready$': function (obj, o, v) {
                    v && (!this.rendered && this.render(TRUE) || this.toggle(TRUE));
                },

                // Style checks
                '^style.classes$': function (obj, o, v, p) {
                    this.rendered && this.tooltip.removeClass(p).addClass(v);
                },
                '^style.(width|height)': function (obj, o, v) {
                    this.rendered && this.tooltip.css(o, v);
                },
                '^style.widget|content.title': function () {
                    this.rendered && this._setWidget();
                },
                '^style.def': function (obj, o, v) {
                    this.rendered && this.tooltip.toggleClass(CLASS_DEFAULT, !!v);
                },

                // Events check
                '^events.(render|show|move|hide|focus|blur)$': function (obj, o, v) {
                    this.rendered && this.tooltip[($.isFunction(v) ? '' : 'un') + 'bind']('tooltip' + o, v);
                },

                // Properties which require event reassignment
                '^(show|hide|position).(event|target|fixed|inactive|leave|distance|viewport|adjust)': function () {
                    if (!this.rendered) {
                        return;
                    }

                    // Set tracking flag
                    var posOptions = this.options.position;
                    this.tooltip.attr('tracking', posOptions.target === 'mouse' && posOptions.adjust.mouse);

                    // Reassign events
                    this._unassignEvents();
                    this._assignEvents();
                }
            }
        };

// Dot notation converter
        function convertNotation(options, notation) {
            var i = 0, obj, option = options,

                // Split notation into array
                levels = notation.split('.');

            // Loop through
            while (option = option[levels[i++]]) {
                if (i < levels.length) {
                    obj = option;
                }
            }

            return [obj || options, levels.pop()];
        }

        PROTOTYPE.get = function (notation) {
            if (this.destroyed) {
                return this;
            }

            var o = convertNotation(this.options, notation.toLowerCase()),
                result = o[0][o[1]];

            return result.precedance ? result.string() : result;
        };

        function setCallback(notation, args) {
            var category, rule, match;

            for (category in this.checks) {
                for (rule in this.checks[category]) {
                    if (match = (new RegExp(rule, 'i')).exec(notation)) {
                        args.push(match);

                        if (category === 'builtin' || this.plugins[category]) {
                            this.checks[category][rule].apply(
                                this.plugins[category] || this, args
                            );
                        }
                    }
                }
            }
        }

        var rmove = /^position\.(my|at|adjust|target|container|viewport)|style|content|show\.ready/i,
            rrender = /^prerender|show\.ready/i;

        PROTOTYPE.set = function (option, value) {
            if (this.destroyed) {
                return this;
            }

            var rendered = this.rendered,
                reposition = FALSE,
                options = this.options,
                checks = this.checks,
                name;

            // Convert singular option/value pair into object form
            if (typeof option === 'string') {
                name = option;
                option = {};
                option[name] = value;
            }
            else {
                option = $.extend({}, option);
            }

            // Set all of the defined options to their new values
            $.each(option, function (notation, value) {
                if (rendered && rrender.test(notation)) {
                    delete option[notation];
                    return;
                }

                // Set new obj value
                var obj = convertNotation(options, notation.toLowerCase()), previous;
                previous = obj[0][obj[1]];
                obj[0][obj[1]] = value && value.nodeType ? $(value) : value;

                // Also check if we need to reposition
                reposition = rmove.test(notation) || reposition;

                // Set the new params for the callback
                option[notation] = [obj[0], obj[1], value, previous];
            });

            // Re-sanitize options
            sanitizeOptions(options);

            /*
	 * Execute any valid callbacks for the set options
	 * Also set positioning flag so we don't get loads of redundant repositioning calls.
	 */
            this.positioning = TRUE;
            $.each(option, $.proxy(setCallback, this));
            this.positioning = FALSE;

            // Update position if needed
            if (this.rendered && this.tooltip[0].offsetWidth > 0 && reposition) {
                this.reposition(options.position.target === 'mouse' ? NULL : this.cache.event);
            }

            return this;
        };
        ;PROTOTYPE._update = function (content, element, reposition) {
            var self = this,
                cache = this.cache;

            // Make sure tooltip is rendered and content is defined. If not return
            if (!this.rendered || !content) {
                return FALSE;
            }

            // Use function to parse content
            if ($.isFunction(content)) {
                content = content.call(this.elements.target, cache.event, this) || '';
            }

            // Handle deferred content
            if ($.isFunction(content.then)) {
                cache.waiting = TRUE;
                return content.then(function (c) {
                    cache.waiting = FALSE;
                    return self._update(c, element);
                }, NULL, function (e) {
                    return self._update(e, element);
                });
            }

            // If content is null... return false
            if (content === FALSE || (!content && content !== '')) {
                return FALSE;
            }

            // Append new content if its a DOM array and show it if hidden
            if (content.jquery && content.length > 0) {
                element.empty().append(
                    content.css({display: 'block', visibility: 'visible'})
                );
            }

            // Content is a regular string, insert the new content
            else {
                element.html(content);
            }

            // Wait for content to be loaded, and reposition
            return this._waitForContent(element).then(function (images) {
                if (self.rendered && self.tooltip[0].offsetWidth > 0) {
                    self.reposition(cache.event, !images.length);
                }
            });
        };

        PROTOTYPE._waitForContent = function (element) {
            var cache = this.cache;

            // Set flag
            cache.waiting = TRUE;

            // If imagesLoaded is included, ensure images have loaded and return promise
            return ($.fn.imagesLoaded ? element.imagesLoaded() : $.Deferred().resolve([]))
                .done(function () {
                    cache.waiting = FALSE;
                })
                .promise();
        };

        PROTOTYPE._updateContent = function (content, reposition) {
            this._update(content, this.elements.content, reposition);
        };

        PROTOTYPE._updateTitle = function (content, reposition) {
            if (this._update(content, this.elements.title, reposition) === FALSE) {
                this._removeTitle(FALSE);
            }
        };

        PROTOTYPE._createTitle = function () {
            var elements = this.elements,
                id = this._id + '-title';

            // Destroy previous title element, if present
            if (elements.titlebar) {
                this._removeTitle();
            }

            // Create title bar and title elements
            elements.titlebar = $('<div />', {
                'class': NAMESPACE + '-titlebar ' + (this.options.style.widget ? createWidgetClass('header') : '')
            })
                .append(
                    elements.title = $('<div />', {
                        'id': id,
                        'class': NAMESPACE + '-title',
                        'aria-atomic': TRUE
                    })
                )
                .insertBefore(elements.content)

                // Button-specific events
                .delegate('.qtip-close', 'mousedown keydown mouseup keyup mouseout', function (event) {
                    $(this).toggleClass('ui-state-active ui-state-focus', event.type.substr(-4) === 'down');
                })
                .delegate('.qtip-close', 'mouseover mouseout', function (event) {
                    $(this).toggleClass('ui-state-hover', event.type === 'mouseover');
                });

            // Create button if enabled
            if (this.options.content.button) {
                this._createButton();
            }
        };

        PROTOTYPE._removeTitle = function (reposition) {
            var elements = this.elements;

            if (elements.title) {
                elements.titlebar.remove();
                elements.titlebar = elements.title = elements.button = NULL;

                // Reposition if enabled
                if (reposition !== FALSE) {
                    this.reposition();
                }
            }
        };
        ;PROTOTYPE._createPosClass = function (my) {
            return NAMESPACE + '-pos-' + (my || this.options.position.my).abbrev();
        };

        PROTOTYPE.reposition = function (event, effect) {
            if (!this.rendered || this.positioning || this.destroyed) {
                return this;
            }

            // Set positioning flag
            this.positioning = TRUE;

            var cache = this.cache,
                tooltip = this.tooltip,
                posOptions = this.options.position,
                target = posOptions.target,
                my = posOptions.my,
                at = posOptions.at,
                viewport = posOptions.viewport,
                container = posOptions.container,
                adjust = posOptions.adjust,
                method = adjust.method.split(' '),
                tooltipWidth = tooltip.outerWidth(FALSE),
                tooltipHeight = tooltip.outerHeight(FALSE),
                targetWidth = 0,
                targetHeight = 0,
                type = tooltip.css('position'),
                position = {left: 0, top: 0},
                visible = tooltip[0].offsetWidth > 0,
                isScroll = event && event.type === 'scroll',
                win = $(window),
                doc = container[0].ownerDocument,
                mouse = this.mouse,
                pluginCalculations, offset, adjusted, newClass;

            // Check if absolute position was passed
            if ($.isArray(target) && target.length === 2) {
                // Force left top and set position
                at = {x: LEFT, y: TOP};
                position = {left: target[0], top: target[1]};
            }

            // Check if mouse was the target
            else if (target === 'mouse') {
                // Force left top to allow flipping
                at = {x: LEFT, y: TOP};

                // Use the mouse origin that caused the show event, if distance hiding is enabled
                if ((!adjust.mouse || this.options.hide.distance) && cache.origin && cache.origin.pageX) {
                    event = cache.origin;
                }

                // Use cached event for resize/scroll events
                else if (!event || (event && (event.type === 'resize' || event.type === 'scroll'))) {
                    event = cache.event;
                }

                // Otherwise, use the cached mouse coordinates if available
                else if (mouse && mouse.pageX) {
                    event = mouse;
                }

                // Calculate body and container offset and take them into account below
                if (type !== 'static') {
                    position = container.offset();
                }
                if (doc.body.offsetWidth !== (window.innerWidth || doc.documentElement.clientWidth)) {
                    offset = $(document.body).offset();
                }

                // Use event coordinates for position
                position = {
                    left: event.pageX - position.left + (offset && offset.left || 0),
                    top: event.pageY - position.top + (offset && offset.top || 0)
                };

                // Scroll events are a pain, some browsers
                if (adjust.mouse && isScroll && mouse) {
                    position.left -= (mouse.scrollX || 0) - win.scrollLeft();
                    position.top -= (mouse.scrollY || 0) - win.scrollTop();
                }
            }

            // Target wasn't mouse or absolute...
            else {
                // Check if event targetting is being used
                if (target === 'event') {
                    if (event && event.target && event.type !== 'scroll' && event.type !== 'resize') {
                        cache.target = $(event.target);
                    }
                    else if (!event.target) {
                        cache.target = this.elements.target;
                    }
                }
                else if (target !== 'event') {
                    cache.target = $(target.jquery ? target : this.elements.target);
                }
                target = cache.target;

                // Parse the target into a jQuery object and make sure there's an element present
                target = $(target).eq(0);
                if (target.length === 0) {
                    return this;
                }

                // Check if window or document is the target
                else if (target[0] === document || target[0] === window) {
                    targetWidth = BROWSER.iOS ? window.innerWidth : target.width();
                    targetHeight = BROWSER.iOS ? window.innerHeight : target.height();

                    if (target[0] === window) {
                        position = {
                            top: (viewport || target).scrollTop(),
                            left: (viewport || target).scrollLeft()
                        };
                    }
                }

                // Check if the target is an <AREA> element
                else if (PLUGINS.imagemap && target.is('area')) {
                    pluginCalculations = PLUGINS.imagemap(this, target, at, PLUGINS.viewport ? method : FALSE);
                }

                // Check if the target is an SVG element
                else if (PLUGINS.svg && target && target[0].ownerSVGElement) {
                    pluginCalculations = PLUGINS.svg(this, target, at, PLUGINS.viewport ? method : FALSE);
                }

                // Otherwise use regular jQuery methods
                else {
                    targetWidth = target.outerWidth(FALSE);
                    targetHeight = target.outerHeight(FALSE);
                    position = target.offset();
                }

                // Parse returned plugin values into proper variables
                if (pluginCalculations) {
                    targetWidth = pluginCalculations.width;
                    targetHeight = pluginCalculations.height;
                    offset = pluginCalculations.offset;
                    position = pluginCalculations.position;
                }

                // Adjust position to take into account offset parents
                position = this.reposition.offset(target, position, container);

                // Adjust for position.fixed tooltips (and also iOS scroll bug in v3.2-4.0 & v4.3-4.3.2)
                if ((BROWSER.iOS > 3.1 && BROWSER.iOS < 4.1) ||
                    (BROWSER.iOS >= 4.3 && BROWSER.iOS < 4.33) ||
                    (!BROWSER.iOS && type === 'fixed')
                ) {
                    position.left -= win.scrollLeft();
                    position.top -= win.scrollTop();
                }

                // Adjust position relative to target
                if (!pluginCalculations || (pluginCalculations && pluginCalculations.adjustable !== FALSE)) {
                    position.left += at.x === RIGHT ? targetWidth : at.x === CENTER ? targetWidth / 2 : 0;
                    position.top += at.y === BOTTOM ? targetHeight : at.y === CENTER ? targetHeight / 2 : 0;
                }
            }

            // Adjust position relative to tooltip
            position.left += adjust.x + (my.x === RIGHT ? -tooltipWidth : my.x === CENTER ? -tooltipWidth / 2 : 0);
            position.top += adjust.y + (my.y === BOTTOM ? -tooltipHeight : my.y === CENTER ? -tooltipHeight / 2 : 0);

            // Use viewport adjustment plugin if enabled
            if (PLUGINS.viewport) {
                adjusted = position.adjusted = PLUGINS.viewport(
                    this, position, posOptions, targetWidth, targetHeight, tooltipWidth, tooltipHeight
                );

                // Apply offsets supplied by positioning plugin (if used)
                if (offset && adjusted.left) {
                    position.left += offset.left;
                }
                if (offset && adjusted.top) {
                    position.top += offset.top;
                }

                // Apply any new 'my' position
                if (adjusted.my) {
                    this.position.my = adjusted.my;
                }
            }

            // Viewport adjustment is disabled, set values to zero
            else {
                position.adjusted = {left: 0, top: 0};
            }

            // Set tooltip position class if it's changed
            if (cache.posClass !== (newClass = this._createPosClass(this.position.my))) {
                tooltip.removeClass(cache.posClass).addClass((cache.posClass = newClass));
            }

            // tooltipmove event
            if (!this._trigger('move', [position, viewport.elem || viewport], event)) {
                return this;
            }
            delete position.adjusted;

            // If effect is disabled, target it mouse, no animation is defined or positioning gives NaN out, set CSS directly
            if (effect === FALSE || !visible || isNaN(position.left) || isNaN(position.top) || target === 'mouse' || !$.isFunction(posOptions.effect)) {
                tooltip.css(position);
            }

            // Use custom function if provided
            else if ($.isFunction(posOptions.effect)) {
                posOptions.effect.call(tooltip, this, $.extend({}, position));
                tooltip.queue(function (next) {
                    // Reset attributes to avoid cross-browser rendering bugs
                    $(this).css({opacity: '', height: ''});
                    if (BROWSER.ie) {
                        this.style.removeAttribute('filter');
                    }

                    next();
                });
            }

            // Set positioning flag
            this.positioning = FALSE;

            return this;
        };

// Custom (more correct for qTip!) offset calculator
        PROTOTYPE.reposition.offset = function (elem, pos, container) {
            if (!container[0]) {
                return pos;
            }

            var ownerDocument = $(elem[0].ownerDocument),
                quirks = !!BROWSER.ie && document.compatMode !== 'CSS1Compat',
                parent = container[0],
                scrolled, position, parentOffset, overflow;

            function scroll(e, i) {
                pos.left += i * e.scrollLeft();
                pos.top += i * e.scrollTop();
            }

            // Compensate for non-static containers offset
            do {
                if ((position = $.css(parent, 'position')) !== 'static') {
                    if (position === 'fixed') {
                        parentOffset = parent.getBoundingClientRect();
                        scroll(ownerDocument, -1);
                    }
                    else {
                        parentOffset = $(parent).position();
                        parentOffset.left += (parseFloat($.css(parent, 'borderLeftWidth')) || 0);
                        parentOffset.top += (parseFloat($.css(parent, 'borderTopWidth')) || 0);
                    }

                    pos.left -= parentOffset.left + (parseFloat($.css(parent, 'marginLeft')) || 0);
                    pos.top -= parentOffset.top + (parseFloat($.css(parent, 'marginTop')) || 0);

                    // If this is the first parent element with an overflow of "scroll" or "auto", store it
                    if (!scrolled && (overflow = $.css(parent, 'overflow')) !== 'hidden' && overflow !== 'visible') {
                        scrolled = $(parent);
                    }
                }
            }
            while ((parent = parent.offsetParent));

            // Compensate for containers scroll if it also has an offsetParent (or in IE quirks mode)
            if (scrolled && (scrolled[0] !== ownerDocument[0] || quirks)) {
                scroll(scrolled, 1);
            }

            return pos;
        };

// Corner class
        var C = (CORNER = PROTOTYPE.reposition.Corner = function (corner, forceY) {
            corner = ('' + corner).replace(/([A-Z])/, ' $1').replace(/middle/gi, CENTER).toLowerCase();
            this.x = (corner.match(/left|right/i) || corner.match(/center/) || ['inherit'])[0].toLowerCase();
            this.y = (corner.match(/top|bottom|center/i) || ['inherit'])[0].toLowerCase();
            this.forceY = !!forceY;

            var f = corner.charAt(0);
            this.precedance = (f === 't' || f === 'b' ? Y : X);
        }).prototype;

        C.invert = function (z, center) {
            this[z] = this[z] === LEFT ? RIGHT : this[z] === RIGHT ? LEFT : center || this[z];
        };

        C.string = function (join) {
            var x = this.x, y = this.y;

            var result = x !== y ?
                (x === 'center' || y !== 'center' && (this.precedance === Y || this.forceY) ?
                        [y, x] : [x, y]
                ) :
                [x];

            return join !== false ? result.join(' ') : result;
        };

        C.abbrev = function () {
            var result = this.string(false);
            return result[0].charAt(0) + (result[1] && result[1].charAt(0) || '');
        };

        C.clone = function () {
            return new CORNER(this.string(), this.forceY);
        };

        ;
        PROTOTYPE.toggle = function (state, event) {
            var cache = this.cache,
                options = this.options,
                tooltip = this.tooltip;

            // Try to prevent flickering when tooltip overlaps show element
            if (event) {
                if ((/over|enter/).test(event.type) && cache.event && (/out|leave/).test(cache.event.type) &&
                    options.show.target.add(event.target).length === options.show.target.length &&
                    tooltip.has(event.relatedTarget).length) {
                    return this;
                }

                // Cache event
                cache.event = $.event.fix(event);
            }

            // If we're currently waiting and we've just hidden... stop it
            this.waiting && !state && (this.hiddenDuringWait = TRUE);

            // Render the tooltip if showing and it isn't already
            if (!this.rendered) {
                return state ? this.render(1) : this;
            }
            else if (this.destroyed || this.disabled) {
                return this;
            }

            var type = state ? 'show' : 'hide',
                opts = this.options[type],
                otherOpts = this.options[!state ? 'show' : 'hide'],
                posOptions = this.options.position,
                contentOptions = this.options.content,
                width = this.tooltip.css('width'),
                visible = this.tooltip.is(':visible'),
                animate = state || opts.target.length === 1,
                sameTarget = !event || opts.target.length < 2 || cache.target[0] === event.target,
                identicalState, allow, showEvent, delay, after;

            // Detect state if valid one isn't provided
            if ((typeof state).search('boolean|number')) {
                state = !visible;
            }

            // Check if the tooltip is in an identical state to the new would-be state
            identicalState = !tooltip.is(':animated') && visible === state && sameTarget;

            // Fire tooltip(show/hide) event and check if destroyed
            allow = !identicalState ? !!this._trigger(type, [90]) : NULL;

            // Check to make sure the tooltip wasn't destroyed in the callback
            if (this.destroyed) {
                return this;
            }

            // If the user didn't stop the method prematurely and we're showing the tooltip, focus it
            if (allow !== FALSE && state) {
                this.focus(event);
            }

            // If the state hasn't changed or the user stopped it, return early
            if (!allow || identicalState) {
                return this;
            }

            // Set ARIA hidden attribute
            $.attr(tooltip[0], 'aria-hidden', !state);

            // Execute state specific properties
            if (state) {
                // Store show origin coordinates
                this.mouse && (cache.origin = $.event.fix(this.mouse));

                // Update tooltip content & title if it's a dynamic function
                if ($.isFunction(contentOptions.text)) {
                    this._updateContent(contentOptions.text, FALSE);
                }
                if ($.isFunction(contentOptions.title)) {
                    this._updateTitle(contentOptions.title, FALSE);
                }

                // Cache mousemove events for positioning purposes (if not already tracking)
                if (!trackingBound && posOptions.target === 'mouse' && posOptions.adjust.mouse) {
                    $(document).bind('mousemove.' + NAMESPACE, this._storeMouse);
                    trackingBound = TRUE;
                }

                // Update the tooltip position (set width first to prevent viewport/max-width issues)
                if (!width) {
                    tooltip.css('width', tooltip.outerWidth(FALSE));
                }
                this.reposition(event, arguments[2]);
                if (!width) {
                    tooltip.css('width', '');
                }

                // Hide other tooltips if tooltip is solo
                if (opts.solo) {
                    (typeof opts.solo === 'string' ? $(opts.solo) : $(SELECTOR, opts.solo))
                        .not(tooltip).not(opts.target).qtip('hide', $.Event('tooltipsolo'));
                }
            }
            else {
                // Clear show timer if we're hiding
                clearTimeout(this.timers.show);

                // Remove cached origin on hide
                delete cache.origin;

                // Remove mouse tracking event if not needed (all tracking qTips are hidden)
                if (trackingBound && !$(SELECTOR + '[tracking="true"]:visible', opts.solo).not(tooltip).length) {
                    $(document).unbind('mousemove.' + NAMESPACE);
                    trackingBound = FALSE;
                }

                // Blur the tooltip
                this.blur(event);
            }

            // Define post-animation, state specific properties
            after = $.proxy(function () {
                if (state) {
                    // Prevent antialias from disappearing in IE by removing filter
                    if (BROWSER.ie) {
                        tooltip[0].style.removeAttribute('filter');
                    }

                    // Remove overflow setting to prevent tip bugs
                    tooltip.css('overflow', '');

                    // Autofocus elements if enabled
                    if (typeof opts.autofocus === 'string') {
                        $(this.options.show.autofocus, tooltip).focus();
                    }

                    // If set, hide tooltip when inactive for delay period
                    this.options.show.target.trigger('qtip-' + this.id + '-inactive');
                }
                else {
                    // Reset CSS states
                    tooltip.css({
                        display: '',
                        visibility: '',
                        opacity: '',
                        left: '',
                        top: ''
                    });
                }

                // tooltipvisible/tooltiphidden events
                this._trigger(state ? 'visible' : 'hidden');
            }, this);

            // If no effect type is supplied, use a simple toggle
            if (opts.effect === FALSE || animate === FALSE) {
                tooltip[type]();
                after();
            }

            // Use custom function if provided
            else if ($.isFunction(opts.effect)) {
                tooltip.stop(1, 1);
                opts.effect.call(tooltip, this);
                tooltip.queue('fx', function (n) {
                    after();
                    n();
                });
            }

            // Use basic fade function by default
            else {
                tooltip.fadeTo(90, state ? 1 : 0, after);
            }

            // If inactive hide method is set, active it
            if (state) {
                opts.target.trigger('qtip-' + this.id + '-inactive');
            }

            return this;
        };

        PROTOTYPE.show = function (event) {
            return this.toggle(TRUE, event);
        };

        PROTOTYPE.hide = function (event) {
            return this.toggle(FALSE, event);
        };
        ;PROTOTYPE.focus = function (event) {
            if (!this.rendered || this.destroyed) {
                return this;
            }

            var qtips = $(SELECTOR),
                tooltip = this.tooltip,
                curIndex = parseInt(tooltip[0].style.zIndex, 10),
                newIndex = QTIP.zindex + qtips.length,
                focusedElem;

            // Only update the z-index if it has changed and tooltip is not already focused
            if (!tooltip.hasClass(CLASS_FOCUS)) {
                // tooltipfocus event
                if (this._trigger('focus', [newIndex], event)) {
                    // Only update z-index's if they've changed
                    if (curIndex !== newIndex) {
                        // Reduce our z-index's and keep them properly ordered
                        qtips.each(function () {
                            if (this.style.zIndex > curIndex) {
                                this.style.zIndex = this.style.zIndex - 1;
                            }
                        });

                        // Fire blur event for focused tooltip
                        qtips.filter('.' + CLASS_FOCUS).qtip('blur', event);
                    }

                    // Set the new z-index
                    tooltip.addClass(CLASS_FOCUS)[0].style.zIndex = newIndex;
                }
            }

            return this;
        };

        PROTOTYPE.blur = function (event) {
            if (!this.rendered || this.destroyed) {
                return this;
            }

            // Set focused status to FALSE
            this.tooltip.removeClass(CLASS_FOCUS);

            // tooltipblur event
            this._trigger('blur', [this.tooltip.css('zIndex')], event);

            return this;
        };
        ;PROTOTYPE.disable = function (state) {
            if (this.destroyed) {
                return this;
            }

            // If 'toggle' is passed, toggle the current state
            if (state === 'toggle') {
                state = !(this.rendered ? this.tooltip.hasClass(CLASS_DISABLED) : this.disabled);
            }

            // Disable if no state passed
            else if (typeof state !== 'boolean') {
                state = TRUE;
            }

            if (this.rendered) {
                this.tooltip.toggleClass(CLASS_DISABLED, state)
                    .attr('aria-disabled', state);
            }

            this.disabled = !!state;

            return this;
        };

        PROTOTYPE.enable = function () {
            return this.disable(FALSE);
        };
        ;PROTOTYPE._createButton = function () {
            var self = this,
                elements = this.elements,
                tooltip = elements.tooltip,
                button = this.options.content.button,
                isString = typeof button === 'string',
                close = isString ? button : 'Close tooltip';

            if (elements.button) {
                elements.button.remove();
            }

            // Use custom button if one was supplied by user, else use default
            if (button.jquery) {
                elements.button = button;
            }
            else {
                elements.button = $('<a />', {
                    'class': 'qtip-close ' + (this.options.style.widget ? '' : NAMESPACE + '-icon'),
                    'title': close,
                    'aria-label': close
                })
                    .prepend(
                        $('<span />', {
                            'class': 'ui-icon ui-icon-close',
                            'html': '&times;'
                        })
                    );
            }

            // Create button and setup attributes
            elements.button.appendTo(elements.titlebar || tooltip)
                .attr('role', 'button')
                .click(function (event) {
                    if (!tooltip.hasClass(CLASS_DISABLED)) {
                        self.hide(event);
                    }
                    return FALSE;
                });
        };

        PROTOTYPE._updateButton = function (button) {
            // Make sure tooltip is rendered and if not, return
            if (!this.rendered) {
                return FALSE;
            }

            var elem = this.elements.button;
            if (button) {
                this._createButton();
            }
            else {
                elem.remove();
            }
        };
        ;// Widget class creator
        function createWidgetClass(cls) {
            return WIDGET.concat('').join(cls ? '-' + cls + ' ' : ' ');
        }

// Widget class setter method
        PROTOTYPE._setWidget = function () {
            var on = this.options.style.widget,
                elements = this.elements,
                tooltip = elements.tooltip,
                disabled = tooltip.hasClass(CLASS_DISABLED);

            tooltip.removeClass(CLASS_DISABLED);
            CLASS_DISABLED = on ? 'ui-state-disabled' : 'qtip-disabled';
            tooltip.toggleClass(CLASS_DISABLED, disabled);

            tooltip.toggleClass('ui-helper-reset ' + createWidgetClass(), on).toggleClass(CLASS_DEFAULT, this.options.style.def && !on);

            if (elements.content) {
                elements.content.toggleClass(createWidgetClass('content'), on);
            }
            if (elements.titlebar) {
                elements.titlebar.toggleClass(createWidgetClass('header'), on);
            }
            if (elements.button) {
                elements.button.toggleClass(NAMESPACE + '-icon', !on);
            }
        };
        ;

        function delay(callback, duration) {
            // If tooltip has displayed, start hide timer
            if (duration > 0) {
                return setTimeout(
                    $.proxy(callback, this), duration
                );
            }
            else {
                callback.call(this);
            }
        }

        function showMethod(event) {
            if (this.tooltip.hasClass(CLASS_DISABLED)) {
                return;
            }

            // Clear hide timers
            clearTimeout(this.timers.show);
            clearTimeout(this.timers.hide);

            // Start show timer
            this.timers.show = delay.call(this,
                function () {
                    this.toggle(TRUE, event);
                },
                this.options.show.delay
            );
        }

        function hideMethod(event) {
            if (this.tooltip.hasClass(CLASS_DISABLED) || this.destroyed) {
                return;
            }

            // Check if new target was actually the tooltip element
            var relatedTarget = $(event.relatedTarget),
                ontoTooltip = relatedTarget.closest(SELECTOR)[0] === this.tooltip[0],
                ontoTarget = relatedTarget[0] === this.options.show.target[0];

            // Clear timers and stop animation queue
            clearTimeout(this.timers.show);
            clearTimeout(this.timers.hide);

            // Prevent hiding if tooltip is fixed and event target is the tooltip.
            // Or if mouse positioning is enabled and cursor momentarily overlaps
            if (this !== relatedTarget[0] &&
                (this.options.position.target === 'mouse' && ontoTooltip) ||
                (this.options.hide.fixed && (
                        (/mouse(out|leave|move)/).test(event.type) && (ontoTooltip || ontoTarget))
                )) {
                try {
                    event.preventDefault();
                    event.stopImmediatePropagation();
                } catch (e) {
                }

                return;
            }

            // If tooltip has displayed, start hide timer
            this.timers.hide = delay.call(this,
                function () {
                    this.toggle(FALSE, event);
                },
                this.options.hide.delay,
                this
            );
        }

        function inactiveMethod(event) {
            if (this.tooltip.hasClass(CLASS_DISABLED) || !this.options.hide.inactive) {
                return;
            }

            // Clear timer
            clearTimeout(this.timers.inactive);

            this.timers.inactive = delay.call(this,
                function () {
                    this.hide(event);
                },
                this.options.hide.inactive
            );
        }

        function repositionMethod(event) {
            if (this.rendered && this.tooltip[0].offsetWidth > 0) {
                this.reposition(event);
            }
        }

// Store mouse coordinates
        PROTOTYPE._storeMouse = function (event) {
            (this.mouse = $.event.fix(event)).type = 'mousemove';
            return this;
        };

// Bind events
        PROTOTYPE._bind = function (targets, events, method, suffix, context) {
            if (!targets || !method || !events.length) {
                return;
            }
            var ns = '.' + this._id + (suffix ? '-' + suffix : '');
            $(targets).bind(
                (events.split ? events : events.join(ns + ' ')) + ns,
                $.proxy(method, context || this)
            );
            return this;
        };
        PROTOTYPE._unbind = function (targets, suffix) {
            targets && $(targets).unbind('.' + this._id + (suffix ? '-' + suffix : ''));
            return this;
        };

// Global delegation helper
        function delegate(selector, events, method) {
            $(document.body).delegate(selector,
                (events.split ? events : events.join('.' + NAMESPACE + ' ')) + '.' + NAMESPACE,
                function () {
                    var api = QTIP.api[$.attr(this, ATTR_ID)];
                    api && !api.disabled && method.apply(api, arguments);
                }
            );
        }

// Event trigger
        PROTOTYPE._trigger = function (type, args, event) {
            var callback = $.Event('tooltip' + type);
            callback.originalEvent = (event && $.extend({}, event)) || this.cache.event || NULL;

            this.triggering = type;
            this.tooltip.trigger(callback, [this].concat(args || []));
            this.triggering = FALSE;

            return !callback.isDefaultPrevented();
        };

        PROTOTYPE._bindEvents = function (showEvents, hideEvents, showTargets, hideTargets, showMethod, hideMethod) {
            // Get tasrgets that lye within both
            var similarTargets = showTargets.filter(hideTargets).add(hideTargets.filter(showTargets)),
                toggleEvents = [];

            // If hide and show targets are the same...
            if (similarTargets.length) {

                // Filter identical show/hide events
                $.each(hideEvents, function (i, type) {
                    var showIndex = $.inArray(type, showEvents);

                    // Both events are identical, remove from both hide and show events
                    // and append to toggleEvents
                    showIndex > -1 && toggleEvents.push(showEvents.splice(showIndex, 1)[0]);
                });

                // Toggle events are special case of identical show/hide events, which happen in sequence
                if (toggleEvents.length) {
                    // Bind toggle events to the similar targets
                    this._bind(similarTargets, toggleEvents, function (event) {
                        var state = this.rendered ? this.tooltip[0].offsetWidth > 0 : false;
                        (state ? hideMethod : showMethod).call(this, event);
                    });

                    // Remove the similar targets from the regular show/hide bindings
                    showTargets = showTargets.not(similarTargets);
                    hideTargets = hideTargets.not(similarTargets);
                }
            }

            // Apply show/hide/toggle events
            this._bind(showTargets, showEvents, showMethod);
            this._bind(hideTargets, hideEvents, hideMethod);
        };

        PROTOTYPE._assignInitialEvents = function (event) {
            var options = this.options,
                showTarget = options.show.target,
                hideTarget = options.hide.target,
                showEvents = options.show.event ? $.trim('' + options.show.event).split(' ') : [],
                hideEvents = options.hide.event ? $.trim('' + options.hide.event).split(' ') : [];

            // Catch remove/removeqtip events on target element to destroy redundant tooltips
            this._bind(this.elements.target, ['remove', 'removeqtip'], function (event) {
                this.destroy(true);
            }, 'destroy');

            /*
	 * Make sure hoverIntent functions properly by using mouseleave as a hide event if
	 * mouseenter/mouseout is used for show.event, even if it isn't in the users options.
	 */
            if (/mouse(over|enter)/i.test(options.show.event) && !/mouse(out|leave)/i.test(options.hide.event)) {
                hideEvents.push('mouseleave');
            }

            /*
	 * Also make sure initial mouse targetting works correctly by caching mousemove coords
	 * on show targets before the tooltip has rendered. Also set onTarget when triggered to
	 * keep mouse tracking working.
	 */
            this._bind(showTarget, 'mousemove', function (event) {
                this._storeMouse(event);
                this.cache.onTarget = TRUE;
            });

            // Define hoverIntent function
            function hoverIntent(event) {
                // Only continue if tooltip isn't disabled
                if (this.disabled || this.destroyed) {
                    return FALSE;
                }

                // Cache the event data
                this.cache.event = event && $.event.fix(event);
                this.cache.target = event && $(event.target);

                // Start the event sequence
                clearTimeout(this.timers.show);
                this.timers.show = delay.call(this,
                    function () {
                        this.render(typeof event === 'object' || options.show.ready);
                    },
                    options.prerender ? 0 : options.show.delay
                );
            }

            // Filter and bind events
            this._bindEvents(showEvents, hideEvents, showTarget, hideTarget, hoverIntent, function () {
                if (!this.timers) {
                    return FALSE;
                }
                clearTimeout(this.timers.show);
            });

            // Prerendering is enabled, create tooltip now
            if (options.show.ready || options.prerender) {
                hoverIntent.call(this, event);
            }
        };

// Event assignment method
        PROTOTYPE._assignEvents = function () {
            var self = this,
                options = this.options,
                posOptions = options.position,

                tooltip = this.tooltip,
                showTarget = options.show.target,
                hideTarget = options.hide.target,
                containerTarget = posOptions.container,
                viewportTarget = posOptions.viewport,
                documentTarget = $(document),
                bodyTarget = $(document.body),
                windowTarget = $(window),

                showEvents = options.show.event ? $.trim('' + options.show.event).split(' ') : [],
                hideEvents = options.hide.event ? $.trim('' + options.hide.event).split(' ') : [];


            // Assign passed event callbacks
            $.each(options.events, function (name, callback) {
                self._bind(tooltip, name === 'toggle' ? ['tooltipshow', 'tooltiphide'] : ['tooltip' + name], callback, null, tooltip);
            });

            // Hide tooltips when leaving current window/frame (but not select/option elements)
            if (/mouse(out|leave)/i.test(options.hide.event) && options.hide.leave === 'window') {
                this._bind(documentTarget, ['mouseout', 'blur'], function (event) {
                    if (!/select|option/.test(event.target.nodeName) && !event.relatedTarget) {
                        this.hide(event);
                    }
                });
            }

            // Enable hide.fixed by adding appropriate class
            if (options.hide.fixed) {
                hideTarget = hideTarget.add(tooltip.addClass(CLASS_FIXED));
            }

            /*
	 * Make sure hoverIntent functions properly by using mouseleave to clear show timer if
	 * mouseenter/mouseout is used for show.event, even if it isn't in the users options.
	 */
            else if (/mouse(over|enter)/i.test(options.show.event)) {
                this._bind(hideTarget, 'mouseleave', function () {
                    clearTimeout(this.timers.show);
                });
            }

            // Hide tooltip on document mousedown if unfocus events are enabled
            if (('' + options.hide.event).indexOf('unfocus') > -1) {
                this._bind(containerTarget.closest('html'), ['mousedown', 'touchstart'], function (event) {
                    var elem = $(event.target),
                        enabled = this.rendered && !this.tooltip.hasClass(CLASS_DISABLED) && this.tooltip[0].offsetWidth > 0,
                        isAncestor = elem.parents(SELECTOR).filter(this.tooltip[0]).length > 0;

                    if (elem[0] !== this.target[0] && elem[0] !== this.tooltip[0] && !isAncestor &&
                        !this.target.has(elem[0]).length && enabled
                    ) {
                        this.hide(event);
                    }
                });
            }

            // Check if the tooltip hides when inactive
            if (typeof options.hide.inactive === 'number') {
                // Bind inactive method to show target(s) as a custom event
                this._bind(showTarget, 'qtip-' + this.id + '-inactive', inactiveMethod, 'inactive');

                // Define events which reset the 'inactive' event handler
                this._bind(hideTarget.add(tooltip), QTIP.inactiveEvents, inactiveMethod);
            }

            // Filter and bind events
            this._bindEvents(showEvents, hideEvents, showTarget, hideTarget, showMethod, hideMethod);

            // Mouse movement bindings
            this._bind(showTarget.add(tooltip), 'mousemove', function (event) {
                // Check if the tooltip hides when mouse is moved a certain distance
                if (typeof options.hide.distance === 'number') {
                    var origin = this.cache.origin || {},
                        limit = this.options.hide.distance,
                        abs = Math.abs;

                    // Check if the movement has gone beyond the limit, and hide it if so
                    if (abs(event.pageX - origin.pageX) >= limit || abs(event.pageY - origin.pageY) >= limit) {
                        this.hide(event);
                    }
                }

                // Cache mousemove coords on show targets
                this._storeMouse(event);
            });

            // Mouse positioning events
            if (posOptions.target === 'mouse') {
                // If mouse adjustment is on...
                if (posOptions.adjust.mouse) {
                    // Apply a mouseleave event so we don't get problems with overlapping
                    if (options.hide.event) {
                        // Track if we're on the target or not
                        this._bind(showTarget, ['mouseenter', 'mouseleave'], function (event) {
                            if (!this.cache) {
                                return FALSE;
                            }
                            this.cache.onTarget = event.type === 'mouseenter';
                        });
                    }

                    // Update tooltip position on mousemove
                    this._bind(documentTarget, 'mousemove', function (event) {
                        // Update the tooltip position only if the tooltip is visible and adjustment is enabled
                        if (this.rendered && this.cache.onTarget && !this.tooltip.hasClass(CLASS_DISABLED) && this.tooltip[0].offsetWidth > 0) {
                            this.reposition(event);
                        }
                    });
                }
            }

            // Adjust positions of the tooltip on window resize if enabled
            if (posOptions.adjust.resize || viewportTarget.length) {
                this._bind($.event.special.resize ? viewportTarget : windowTarget, 'resize', repositionMethod);
            }

            // Adjust tooltip position on scroll of the window or viewport element if present
            if (posOptions.adjust.scroll) {
                this._bind(windowTarget.add(posOptions.container), 'scroll', repositionMethod);
            }
        };

// Un-assignment method
        PROTOTYPE._unassignEvents = function () {
            var options = this.options,
                showTargets = options.show.target,
                hideTargets = options.hide.target,
                targets = $.grep([
                    this.elements.target[0],
                    this.rendered && this.tooltip[0],
                    options.position.container[0],
                    options.position.viewport[0],
                    options.position.container.closest('html')[0], // unfocus
                    window,
                    document
                ], function (i) {
                    return typeof i === 'object';
                });

            // Add show and hide targets if they're valid
            if (showTargets && showTargets.toArray) {
                targets = targets.concat(showTargets.toArray());
            }
            if (hideTargets && hideTargets.toArray) {
                targets = targets.concat(hideTargets.toArray());
            }

            // Unbind the events
            this._unbind(targets)
                ._unbind(targets, 'destroy')
                ._unbind(targets, 'inactive');
        };

// Apply common event handlers using delegate (avoids excessive .bind calls!)
        $(function () {
            delegate(SELECTOR, ['mouseenter', 'mouseleave'], function (event) {
                var state = event.type === 'mouseenter',
                    tooltip = $(event.currentTarget),
                    target = $(event.relatedTarget || event.target),
                    options = this.options;

                // On mouseenter...
                if (state) {
                    // Focus the tooltip on mouseenter (z-index stacking)
                    this.focus(event);

                    // Clear hide timer on tooltip hover to prevent it from closing
                    tooltip.hasClass(CLASS_FIXED) && !tooltip.hasClass(CLASS_DISABLED) && clearTimeout(this.timers.hide);
                }

                // On mouseleave...
                else {
                    // When mouse tracking is enabled, hide when we leave the tooltip and not onto the show target (if a hide event is set)
                    if (options.position.target === 'mouse' && options.position.adjust.mouse &&
                        options.hide.event && options.show.target && !target.closest(options.show.target[0]).length) {
                        this.hide(event);
                    }
                }

                // Add hover class
                tooltip.toggleClass(CLASS_HOVER, state);
            });

            // Define events which reset the 'inactive' event handler
            delegate('[' + ATTR_ID + ']', INACTIVE_EVENTS, inactiveMethod);
        });
        ;// Initialization method
        function init(elem, id, opts) {
            var obj, posOptions, attr, config, title,

                // Setup element references
                docBody = $(document.body),

                // Use document body instead of document element if needed
                newTarget = elem[0] === document ? docBody : elem,

                // Grab metadata from element if plugin is present
                metadata = (elem.metadata) ? elem.metadata(opts.metadata) : NULL,

                // If metadata type if HTML5, grab 'name' from the object instead, or use the regular data object otherwise
                metadata5 = opts.metadata.type === 'html5' && metadata ? metadata[opts.metadata.name] : NULL,

                // Grab data from metadata.name (or data-qtipopts as fallback) using .data() method,
                html5 = elem.data(opts.metadata.name || 'qtipopts');

            // If we don't get an object returned attempt to parse it manualyl without parseJSON
            try {
                html5 = typeof html5 === 'string' ? $.parseJSON(html5) : html5;
            } catch (e) {
            }

            // Merge in and sanitize metadata
            config = $.extend(TRUE, {}, QTIP.defaults, opts,
                typeof html5 === 'object' ? sanitizeOptions(html5) : NULL,
                sanitizeOptions(metadata5 || metadata));

            // Re-grab our positioning options now we've merged our metadata and set id to passed value
            posOptions = config.position;
            config.id = id;

            // Setup missing content if none is detected
            if (typeof config.content.text === 'boolean') {
                attr = elem.attr(config.content.attr);

                // Grab from supplied attribute if available
                if (config.content.attr !== FALSE && attr) {
                    config.content.text = attr;
                }

                // No valid content was found, abort render
                else {
                    return FALSE;
                }
            }

            // Setup target options
            if (!posOptions.container.length) {
                posOptions.container = docBody;
            }
            if (posOptions.target === FALSE) {
                posOptions.target = newTarget;
            }
            if (config.show.target === FALSE) {
                config.show.target = newTarget;
            }
            if (config.show.solo === TRUE) {
                config.show.solo = posOptions.container.closest('body');
            }
            if (config.hide.target === FALSE) {
                config.hide.target = newTarget;
            }
            if (config.position.viewport === TRUE) {
                config.position.viewport = posOptions.container;
            }

            // Ensure we only use a single container
            posOptions.container = posOptions.container.eq(0);

            // Convert position corner values into x and y strings
            posOptions.at = new CORNER(posOptions.at, TRUE);
            posOptions.my = new CORNER(posOptions.my);

            // Destroy previous tooltip if overwrite is enabled, or skip element if not
            if (elem.data(NAMESPACE)) {
                if (config.overwrite) {
                    elem.qtip('destroy', true);
                }
                else if (config.overwrite === FALSE) {
                    return FALSE;
                }
            }

            // Add has-qtip attribute
            elem.attr(ATTR_HAS, id);

            // Remove title attribute and store it if present
            if (config.suppress && (title = elem.attr('title'))) {
                // Final attr call fixes event delegatiom and IE default tooltip showing problem
                elem.removeAttr('title').attr(oldtitle, title).attr('title', '');
            }

            // Initialize the tooltip and add API reference
            obj = new QTip(elem, config, id, !!attr);
            elem.data(NAMESPACE, obj);

            return obj;
        }

// jQuery $.fn extension method
        QTIP = $.fn.qtip = function (options, notation, newValue) {
            var command = ('' + options).toLowerCase(), // Parse command
                returned = NULL,
                args = $.makeArray(arguments).slice(1),
                event = args[args.length - 1],
                opts = this[0] ? $.data(this[0], NAMESPACE) : NULL;

            // Check for API request
            if ((!arguments.length && opts) || command === 'api') {
                return opts;
            }

            // Execute API command if present
            else if (typeof options === 'string') {
                this.each(function () {
                    var api = $.data(this, NAMESPACE);
                    if (!api) {
                        return TRUE;
                    }

                    // Cache the event if possible
                    if (event && event.timeStamp) {
                        api.cache.event = event;
                    }

                    // Check for specific API commands
                    if (notation && (command === 'option' || command === 'options')) {
                        if (newValue !== undefined || $.isPlainObject(notation)) {
                            api.set(notation, newValue);
                        }
                        else {
                            returned = api.get(notation);
                            return FALSE;
                        }
                    }

                    // Execute API command
                    else if (api[command]) {
                        api[command].apply(api, args);
                    }
                });

                return returned !== NULL ? returned : this;
            }

            // No API commands. validate provided options and setup qTips
            else if (typeof options === 'object' || !arguments.length) {
                // Sanitize options first
                opts = sanitizeOptions($.extend(TRUE, {}, options));

                return this.each(function (i) {
                    var api, id;

                    // Find next available ID, or use custom ID if provided
                    id = $.isArray(opts.id) ? opts.id[i] : opts.id;
                    id = !id || id === FALSE || id.length < 1 || QTIP.api[id] ? QTIP.nextid++ : id;

                    // Initialize the qTip and re-grab newly sanitized options
                    api = init($(this), id, opts);
                    if (api === FALSE) {
                        return TRUE;
                    }
                    else {
                        QTIP.api[id] = api;
                    }

                    // Initialize plugins
                    $.each(PLUGINS, function () {
                        if (this.initialize === 'initialize') {
                            this(api);
                        }
                    });

                    // Assign initial pre-render events
                    api._assignInitialEvents(event);
                });
            }
        };

// Expose class
        $.qtip = QTip;

// Populated in render method
        QTIP.api = {};
        ;$.each({
            /* Allow other plugins to successfully retrieve the title of an element with a qTip applied */
            attr: function (attr, val) {
                if (this.length) {
                    var self = this[0],
                        title = 'title',
                        api = $.data(self, 'qtip');

                    if (attr === title && api && typeof api === 'object' && api.options.suppress) {
                        if (arguments.length < 2) {
                            return $.attr(self, oldtitle);
                        }

                        // If qTip is rendered and title was originally used as content, update it
                        if (api && api.options.content.attr === title && api.cache.attr) {
                            api.set('content.text', val);
                        }

                        // Use the regular attr method to set, then cache the result
                        return this.attr(oldtitle, val);
                    }
                }

                return $.fn['attr' + replaceSuffix].apply(this, arguments);
            },

            /* Allow clone to correctly retrieve cached title attributes */
            clone: function (keepData) {
                var titles = $([]), title = 'title',

                    // Clone our element using the real clone method
                    elems = $.fn['clone' + replaceSuffix].apply(this, arguments);

                // Grab all elements with an oldtitle set, and change it to regular title attribute, if keepData is false
                if (!keepData) {
                    elems.filter('[' + oldtitle + ']').attr('title', function () {
                        return $.attr(this, oldtitle);
                    })
                        .removeAttr(oldtitle);
                }

                return elems;
            }
        }, function (name, func) {
            if (!func || $.fn[name + replaceSuffix]) {
                return TRUE;
            }

            var old = $.fn[name + replaceSuffix] = $.fn[name];
            $.fn[name] = function () {
                return func.apply(this, arguments) || old.apply(this, arguments);
            };
        });

        /* Fire off 'removeqtip' handler in $.cleanData if jQuery UI not present (it already does similar).
 * This snippet is taken directly from jQuery UI source code found here:
 *     http://code.jquery.com/ui/jquery-ui-git.js
 */
        if (!$.ui) {
            $['cleanData' + replaceSuffix] = $.cleanData;
            $.cleanData = function (elems) {
                for (var i = 0, elem; (elem = $(elems[i])).length; i++) {
                    if (elem.attr(ATTR_HAS)) {
                        try {
                            elem.triggerHandler('removeqtip');
                        }
                        catch (e) {
                        }
                    }
                }
                $['cleanData' + replaceSuffix].apply(this, arguments);
            };
        }
        ;// qTip version
        QTIP.version = '2.2.1';

// Base ID for all qTips
        QTIP.nextid = 0;

// Inactive events array
        QTIP.inactiveEvents = INACTIVE_EVENTS;

// Base z-index for all qTips
        QTIP.zindex = 15000;

// Define configuration defaults
        QTIP.defaults = {
            prerender: FALSE,
            id: FALSE,
            overwrite: TRUE,
            suppress: TRUE,
            content: {
                text: TRUE,
                attr: 'title',
                title: FALSE,
                button: FALSE
            },
            position: {
                my: 'top left',
                at: 'bottom right',
                target: FALSE,
                container: FALSE,
                viewport: FALSE,
                adjust: {
                    x: 0, y: 0,
                    mouse: TRUE,
                    scroll: TRUE,
                    resize: TRUE,
                    method: 'flipinvert flipinvert'
                },
                effect: function (api, pos, viewport) {
                    $(this).animate(pos, {
                        duration: 200,
                        queue: FALSE
                    });
                }
            },
            show: {
                target: FALSE,
                event: 'mouseenter',
                effect: TRUE,
                delay: 90,
                solo: FALSE,
                ready: FALSE,
                autofocus: FALSE
            },
            hide: {
                target: FALSE,
                event: 'mouseleave',
                effect: TRUE,
                delay: 0,
                fixed: FALSE,
                inactive: FALSE,
                leave: 'window',
                distance: FALSE
            },
            style: {
                classes: '',
                widget: FALSE,
                width: FALSE,
                height: FALSE,
                def: TRUE
            },
            events: {
                render: NULL,
                move: NULL,
                show: NULL,
                hide: NULL,
                toggle: NULL,
                visible: NULL,
                hidden: NULL,
                focus: NULL,
                blur: NULL
            }
        };
        ;var TIP,

// .bind()/.on() namespace
            TIPNS = '.qtip-tip',

// Common CSS strings
            MARGIN = 'margin',
            BORDER = 'border',
            COLOR = 'color',
            BG_COLOR = 'background-color',
            TRANSPARENT = 'transparent',
            IMPORTANT = ' !important',

// Check if the browser supports <canvas/> elements
            HASCANVAS = !!document.createElement('canvas').getContext,

// Invalid colour values used in parseColours()
            INVALID = /rgba?\(0, 0, 0(, 0)?\)|transparent|#123456/i;

// Camel-case method, taken from jQuery source
// http://code.jquery.com/jquery-1.8.0.js
        function camel(s) {
            return s.charAt(0).toUpperCase() + s.slice(1);
        }

        /*
 * Modified from Modernizr's testPropsAll()
 * http://modernizr.com/downloads/modernizr-latest.js
 */
        var cssProps = {}, cssPrefixes = ["Webkit", "O", "Moz", "ms"];

        function vendorCss(elem, prop) {
            var ucProp = prop.charAt(0).toUpperCase() + prop.slice(1),
                props = (prop + ' ' + cssPrefixes.join(ucProp + ' ') + ucProp).split(' '),
                cur, val, i = 0;

            // If the property has already been mapped...
            if (cssProps[prop]) {
                return elem.css(cssProps[prop]);
            }

            while ((cur = props[i++])) {
                if ((val = elem.css(cur)) !== undefined) {
                    return cssProps[prop] = cur, val;
                }
            }
        }

// Parse a given elements CSS property into an int
        function intCss(elem, prop) {
            return Math.ceil(parseFloat(vendorCss(elem, prop)));
        }


// VML creation (for IE only)
        if (!HASCANVAS) {
            var createVML = function (tag, props, style) {
                return '<qtipvml:' + tag + ' xmlns="urn:schemas-microsoft.com:vml" class="qtip-vml" ' + (props || '') +
                    ' style="behavior: url(#default#VML); ' + (style || '') + '" />';
            };
        }

// Canvas only definitions
        else {
            var PIXEL_RATIO = window.devicePixelRatio || 1,
                BACKING_STORE_RATIO = (function () {
                    var context = document.createElement('canvas').getContext('2d');
                    return context.backingStorePixelRatio || context.webkitBackingStorePixelRatio || context.mozBackingStorePixelRatio ||
                        context.msBackingStorePixelRatio || context.oBackingStorePixelRatio || 1;
                }()),
                SCALE = PIXEL_RATIO / BACKING_STORE_RATIO;
        }


        function Tip(qtip, options) {
            this._ns = 'tip';
            this.options = options;
            this.offset = options.offset;
            this.size = [options.width, options.height];

            // Initialize
            this.init((this.qtip = qtip));
        }

        $.extend(Tip.prototype, {
            init: function (qtip) {
                var context, tip;

                // Create tip element and prepend to the tooltip
                tip = this.element = qtip.elements.tip = $('<div />', {'class': NAMESPACE + '-tip'}).prependTo(qtip.tooltip);

                // Create tip drawing element(s)
                if (HASCANVAS) {
                    // save() as soon as we create the canvas element so FF2 doesn't bork on our first restore()!
                    context = $('<canvas />').appendTo(this.element)[0].getContext('2d');

                    // Setup constant parameters
                    context.lineJoin = 'miter';
                    context.miterLimit = 100000;
                    context.save();
                }
                else {
                    context = createVML('shape', 'coordorigin="0,0"', 'position:absolute;');
                    this.element.html(context + context);

                    // Prevent mousing down on the tip since it causes problems with .live() handling in IE due to VML
                    qtip._bind($('*', tip).add(tip), ['click', 'mousedown'], function (event) {
                        event.stopPropagation();
                    }, this._ns);
                }

                // Bind update events
                qtip._bind(qtip.tooltip, 'tooltipmove', this.reposition, this._ns, this);

                // Create it
                this.create();
            },

            _swapDimensions: function () {
                this.size[0] = this.options.height;
                this.size[1] = this.options.width;
            },
            _resetDimensions: function () {
                this.size[0] = this.options.width;
                this.size[1] = this.options.height;
            },

            _useTitle: function (corner) {
                var titlebar = this.qtip.elements.titlebar;
                return titlebar && (
                    corner.y === TOP || (corner.y === CENTER && this.element.position().top + (this.size[1] / 2) + this.options.offset < titlebar.outerHeight(TRUE))
                );
            },

            _parseCorner: function (corner) {
                var my = this.qtip.options.position.my;

                // Detect corner and mimic properties
                if (corner === FALSE || my === FALSE) {
                    corner = FALSE;
                }
                else if (corner === TRUE) {
                    corner = new CORNER(my.string());
                }
                else if (!corner.string) {
                    corner = new CORNER(corner);
                    corner.fixed = TRUE;
                }

                return corner;
            },

            _parseWidth: function (corner, side, use) {
                var elements = this.qtip.elements,
                    prop = BORDER + camel(side) + 'Width';

                return (use ? intCss(use, prop) : (
                    intCss(elements.content, prop) ||
                    intCss(this._useTitle(corner) && elements.titlebar || elements.content, prop) ||
                    intCss(elements.tooltip, prop)
                )) || 0;
            },

            _parseRadius: function (corner) {
                var elements = this.qtip.elements,
                    prop = BORDER + camel(corner.y) + camel(corner.x) + 'Radius';

                return BROWSER.ie < 9 ? 0 :
                    intCss(this._useTitle(corner) && elements.titlebar || elements.content, prop) ||
                    intCss(elements.tooltip, prop) || 0;
            },

            _invalidColour: function (elem, prop, compare) {
                var val = elem.css(prop);
                return !val || (compare && val === elem.css(compare)) || INVALID.test(val) ? FALSE : val;
            },

            _parseColours: function (corner) {
                var elements = this.qtip.elements,
                    tip = this.element.css('cssText', ''),
                    borderSide = BORDER + camel(corner[corner.precedance]) + camel(COLOR),
                    colorElem = this._useTitle(corner) && elements.titlebar || elements.content,
                    css = this._invalidColour, color = [];

                // Attempt to detect the background colour from various elements, left-to-right precedance
                color[0] = css(tip, BG_COLOR) || css(colorElem, BG_COLOR) || css(elements.content, BG_COLOR) ||
                    css(elements.tooltip, BG_COLOR) || tip.css(BG_COLOR);

                // Attempt to detect the correct border side colour from various elements, left-to-right precedance
                color[1] = css(tip, borderSide, COLOR) || css(colorElem, borderSide, COLOR) ||
                    css(elements.content, borderSide, COLOR) || css(elements.tooltip, borderSide, COLOR) || elements.tooltip.css(borderSide);

                // Reset background and border colours
                $('*', tip).add(tip).css('cssText', BG_COLOR + ':' + TRANSPARENT + IMPORTANT + ';' + BORDER + ':0' + IMPORTANT + ';');

                return color;
            },

            _calculateSize: function (corner) {
                var y = corner.precedance === Y,
                    width = this.options['width'],
                    height = this.options['height'],
                    isCenter = corner.abbrev() === 'c',
                    base = (y ? width : height) * (isCenter ? 0.5 : 1),
                    pow = Math.pow,
                    round = Math.round,
                    bigHyp, ratio, result,

                    smallHyp = Math.sqrt(pow(base, 2) + pow(height, 2)),
                    hyp = [(this.border / base) * smallHyp, (this.border / height) * smallHyp];

                hyp[2] = Math.sqrt(pow(hyp[0], 2) - pow(this.border, 2));
                hyp[3] = Math.sqrt(pow(hyp[1], 2) - pow(this.border, 2));

                bigHyp = smallHyp + hyp[2] + hyp[3] + (isCenter ? 0 : hyp[0]);
                ratio = bigHyp / smallHyp;

                result = [round(ratio * width), round(ratio * height)];
                return y ? result : result.reverse();
            },

            // Tip coordinates calculator
            _calculateTip: function (corner, size, scale) {
                scale = scale || 1;
                size = size || this.size;

                var width = size[0] * scale,
                    height = size[1] * scale,
                    width2 = Math.ceil(width / 2), height2 = Math.ceil(height / 2),

                    // Define tip coordinates in terms of height and width values
                    tips = {
                        br: [0, 0, width, height, width, 0],
                        bl: [0, 0, width, 0, 0, height],
                        tr: [0, height, width, 0, width, height],
                        tl: [0, 0, 0, height, width, height],
                        tc: [0, height, width2, 0, width, height],
                        bc: [0, 0, width, 0, width2, height],
                        rc: [0, 0, width, height2, 0, height],
                        lc: [width, 0, width, height, 0, height2]
                    };

                // Set common side shapes
                tips.lt = tips.br;
                tips.rt = tips.bl;
                tips.lb = tips.tr;
                tips.rb = tips.tl;

                return tips[corner.abbrev()];
            },

            // Tip coordinates drawer (canvas)
            _drawCoords: function (context, coords) {
                context.beginPath();
                context.moveTo(coords[0], coords[1]);
                context.lineTo(coords[2], coords[3]);
                context.lineTo(coords[4], coords[5]);
                context.closePath();
            },

            create: function () {
                // Determine tip corner
                var c = this.corner = (HASCANVAS || BROWSER.ie) && this._parseCorner(this.options.corner);

                // If we have a tip corner...
                if ((this.enabled = !!this.corner && this.corner.abbrev() !== 'c')) {
                    // Cache it
                    this.qtip.cache.corner = c.clone();

                    // Create it
                    this.update();
                }

                // Toggle tip element
                this.element.toggle(this.enabled);

                return this.corner;
            },

            update: function (corner, position) {
                if (!this.enabled) {
                    return this;
                }

                var elements = this.qtip.elements,
                    tip = this.element,
                    inner = tip.children(),
                    options = this.options,
                    curSize = this.size,
                    mimic = options.mimic,
                    round = Math.round,
                    color, precedance, context,
                    coords, bigCoords, translate, newSize, border, BACKING_STORE_RATIO;

                // Re-determine tip if not already set
                if (!corner) {
                    corner = this.qtip.cache.corner || this.corner;
                }

                // Use corner property if we detect an invalid mimic value
                if (mimic === FALSE) {
                    mimic = corner;
                }

                // Otherwise inherit mimic properties from the corner object as necessary
                else {
                    mimic = new CORNER(mimic);
                    mimic.precedance = corner.precedance;

                    if (mimic.x === 'inherit') {
                        mimic.x = corner.x;
                    }
                    else if (mimic.y === 'inherit') {
                        mimic.y = corner.y;
                    }
                    else if (mimic.x === mimic.y) {
                        mimic[corner.precedance] = corner[corner.precedance];
                    }
                }
                precedance = mimic.precedance;

                // Ensure the tip width.height are relative to the tip position
                if (corner.precedance === X) {
                    this._swapDimensions();
                }
                else {
                    this._resetDimensions();
                }

                // Update our colours
                color = this.color = this._parseColours(corner);

                // Detect border width, taking into account colours
                if (color[1] !== TRANSPARENT) {
                    // Grab border width
                    border = this.border = this._parseWidth(corner, corner[corner.precedance]);

                    // If border width isn't zero, use border color as fill if it's not invalid (1.0 style tips)
                    if (options.border && border < 1 && !INVALID.test(color[1])) {
                        color[0] = color[1];
                    }

                    // Set border width (use detected border width if options.border is true)
                    this.border = border = options.border !== TRUE ? options.border : border;
                }

                // Border colour was invalid, set border to zero
                else {
                    this.border = border = 0;
                }

                // Determine tip size
                newSize = this.size = this._calculateSize(corner);
                tip.css({
                    width: newSize[0],
                    height: newSize[1],
                    lineHeight: newSize[1] + 'px'
                });

                // Calculate tip translation
                if (corner.precedance === Y) {
                    translate = [
                        round(mimic.x === LEFT ? border : mimic.x === RIGHT ? newSize[0] - curSize[0] - border : (newSize[0] - curSize[0]) / 2),
                        round(mimic.y === TOP ? newSize[1] - curSize[1] : 0)
                    ];
                }
                else {
                    translate = [
                        round(mimic.x === LEFT ? newSize[0] - curSize[0] : 0),
                        round(mimic.y === TOP ? border : mimic.y === BOTTOM ? newSize[1] - curSize[1] - border : (newSize[1] - curSize[1]) / 2)
                    ];
                }

                // Canvas drawing implementation
                if (HASCANVAS) {
                    // Grab canvas context and clear/save it
                    context = inner[0].getContext('2d');
                    context.restore();
                    context.save();
                    context.clearRect(0, 0, 6000, 6000);

                    // Calculate coordinates
                    coords = this._calculateTip(mimic, curSize, SCALE);
                    bigCoords = this._calculateTip(mimic, this.size, SCALE);

                    // Set the canvas size using calculated size
                    inner.attr(WIDTH, newSize[0] * SCALE).attr(HEIGHT, newSize[1] * SCALE);
                    inner.css(WIDTH, newSize[0]).css(HEIGHT, newSize[1]);

                    // Draw the outer-stroke tip
                    this._drawCoords(context, bigCoords);
                    context.fillStyle = color[1];
                    context.fill();

                    // Draw the actual tip
                    context.translate(translate[0] * SCALE, translate[1] * SCALE);
                    this._drawCoords(context, coords);
                    context.fillStyle = color[0];
                    context.fill();
                }

                // VML (IE Proprietary implementation)
                else {
                    // Calculate coordinates
                    coords = this._calculateTip(mimic);

                    // Setup coordinates string
                    coords = 'm' + coords[0] + ',' + coords[1] + ' l' + coords[2] +
                        ',' + coords[3] + ' ' + coords[4] + ',' + coords[5] + ' xe';

                    // Setup VML-specific offset for pixel-perfection
                    translate[2] = border && /^(r|b)/i.test(corner.string()) ?
                        BROWSER.ie === 8 ? 2 : 1 : 0;

                    // Set initial CSS
                    inner.css({
                        coordsize: (newSize[0] + border) + ' ' + (newSize[1] + border),
                        antialias: '' + (mimic.string().indexOf(CENTER) > -1),
                        left: translate[0] - (translate[2] * Number(precedance === X)),
                        top: translate[1] - (translate[2] * Number(precedance === Y)),
                        width: newSize[0] + border,
                        height: newSize[1] + border
                    })
                        .each(function (i) {
                            var $this = $(this);

                            // Set shape specific attributes
                            $this[$this.prop ? 'prop' : 'attr']({
                                coordsize: (newSize[0] + border) + ' ' + (newSize[1] + border),
                                path: coords,
                                fillcolor: color[0],
                                filled: !!i,
                                stroked: !i
                            })
                                .toggle(!!(border || i));

                            // Check if border is enabled and add stroke element
                            !i && $this.html(createVML(
                                'stroke', 'weight="' + (border * 2) + 'px" color="' + color[1] + '" miterlimit="1000" joinstyle="miter"'
                            ));
                        });
                }

                // Opera bug #357 - Incorrect tip position
                // https://github.com/Craga89/qTip2/issues/367
                window.opera && setTimeout(function () {
                    elements.tip.css({
                        display: 'inline-block',
                        visibility: 'visible'
                    });
                }, 1);

                // Position if needed
                if (position !== FALSE) {
                    this.calculate(corner, newSize);
                }
            },

            calculate: function (corner, size) {
                if (!this.enabled) {
                    return FALSE;
                }

                var self = this,
                    elements = this.qtip.elements,
                    tip = this.element,
                    userOffset = this.options.offset,
                    isWidget = elements.tooltip.hasClass('ui-widget'),
                    position = {},
                    precedance, corners;

                // Inherit corner if not provided
                corner = corner || this.corner;
                precedance = corner.precedance;

                // Determine which tip dimension to use for adjustment
                size = size || this._calculateSize(corner);

                // Setup corners and offset array
                corners = [corner.x, corner.y];
                if (precedance === X) {
                    corners.reverse();
                }

                // Calculate tip position
                $.each(corners, function (i, side) {
                    var b, bc, br;

                    if (side === CENTER) {
                        b = precedance === Y ? LEFT : TOP;
                        position[b] = '50%';
                        position[MARGIN + '-' + b] = -Math.round(size[precedance === Y ? 0 : 1] / 2) + userOffset;
                    }
                    else {
                        b = self._parseWidth(corner, side, elements.tooltip);
                        bc = self._parseWidth(corner, side, elements.content);
                        br = self._parseRadius(corner);

                        position[side] = Math.max(-self.border, i ? bc : (userOffset + (br > b ? br : -b)));
                    }
                });

                // Adjust for tip size
                position[corner[precedance]] -= size[precedance === X ? 0 : 1];

                // Set and return new position
                tip.css({margin: '', top: '', bottom: '', left: '', right: ''}).css(position);
                return position;
            },

            reposition: function (event, api, pos, viewport) {
                if (!this.enabled) {
                    return;
                }

                var cache = api.cache,
                    newCorner = this.corner.clone(),
                    adjust = pos.adjusted,
                    method = api.options.position.adjust.method.split(' '),
                    horizontal = method[0],
                    vertical = method[1] || method[0],
                    shift = {left: FALSE, top: FALSE, x: 0, y: 0},
                    offset, css = {}, props;

                function shiftflip(direction, precedance, popposite, side, opposite) {
                    // Horizontal - Shift or flip method
                    if (direction === SHIFT && newCorner.precedance === precedance && adjust[side] && newCorner[popposite] !== CENTER) {
                        newCorner.precedance = newCorner.precedance === X ? Y : X;
                    }
                    else if (direction !== SHIFT && adjust[side]) {
                        newCorner[precedance] = newCorner[precedance] === CENTER ?
                            (adjust[side] > 0 ? side : opposite) : (newCorner[precedance] === side ? opposite : side);
                    }
                }

                function shiftonly(xy, side, opposite) {
                    if (newCorner[xy] === CENTER) {
                        css[MARGIN + '-' + side] = shift[xy] = offset[MARGIN + '-' + side] - adjust[side];
                    }
                    else {
                        props = offset[opposite] !== undefined ?
                            [adjust[side], -offset[side]] : [-adjust[side], offset[side]];

                        if ((shift[xy] = Math.max(props[0], props[1])) > props[0]) {
                            pos[side] -= adjust[side];
                            shift[side] = FALSE;
                        }

                        css[offset[opposite] !== undefined ? opposite : side] = shift[xy];
                    }
                }

                // If our tip position isn't fixed e.g. doesn't adjust with viewport...
                if (this.corner.fixed !== TRUE) {
                    // Perform shift/flip adjustments
                    shiftflip(horizontal, X, Y, LEFT, RIGHT);
                    shiftflip(vertical, Y, X, TOP, BOTTOM);

                    // Update and redraw the tip if needed (check cached details of last drawn tip)
                    if (newCorner.string() !== cache.corner.string() || cache.cornerTop !== adjust.top || cache.cornerLeft !== adjust.left) {
                        this.update(newCorner, FALSE);
                    }
                }

                // Setup tip offset properties
                offset = this.calculate(newCorner);

                // Readjust offset object to make it left/top
                if (offset.right !== undefined) {
                    offset.left = -offset.right;
                }
                if (offset.bottom !== undefined) {
                    offset.top = -offset.bottom;
                }
                offset.user = this.offset;

                // Perform shift adjustments
                if (shift.left = (horizontal === SHIFT && !!adjust.left)) {
                    shiftonly(X, LEFT, RIGHT);
                }
                if (shift.top = (vertical === SHIFT && !!adjust.top)) {
                    shiftonly(Y, TOP, BOTTOM);
                }

                /*
		* If the tip is adjusted in both dimensions, or in a
		* direction that would cause it to be anywhere but the
		* outer border, hide it!
		*/
                this.element.css(css).toggle(
                    !((shift.x && shift.y) || (newCorner.x === CENTER && shift.y) || (newCorner.y === CENTER && shift.x))
                );

                // Adjust position to accomodate tip dimensions
                pos.left -= offset.left.charAt ? offset.user :
                    horizontal !== SHIFT || shift.top || !shift.left && !shift.top ? offset.left + this.border : 0;
                pos.top -= offset.top.charAt ? offset.user :
                    vertical !== SHIFT || shift.left || !shift.left && !shift.top ? offset.top + this.border : 0;

                // Cache details
                cache.cornerLeft = adjust.left;
                cache.cornerTop = adjust.top;
                cache.corner = newCorner.clone();
            },

            destroy: function () {
                // Unbind events
                this.qtip._unbind(this.qtip.tooltip, this._ns);

                // Remove the tip element(s)
                if (this.qtip.elements.tip) {
                    this.qtip.elements.tip.find('*')
                        .remove().end().remove();
                }
            }
        });

        TIP = PLUGINS.tip = function (api) {
            return new Tip(api, api.options.style.tip);
        };

// Initialize tip on render
        TIP.initialize = 'render';

// Setup plugin sanitization options
        TIP.sanitize = function (options) {
            if (options.style && 'tip' in options.style) {
                var opts = options.style.tip;
                if (typeof opts !== 'object') {
                    opts = options.style.tip = {corner: opts};
                }
                if (!(/string|boolean/i).test(typeof opts.corner)) {
                    opts.corner = TRUE;
                }
            }
        };

// Add new option checks for the plugin
        CHECKS.tip = {
            '^position.my|style.tip.(corner|mimic|border)$': function () {
                // Make sure a tip can be drawn
                this.create();

                // Reposition the tooltip
                this.qtip.reposition();
            },
            '^style.tip.(height|width)$': function (obj) {
                // Re-set dimensions and redraw the tip
                this.size = [obj.width, obj.height];
                this.update();

                // Reposition the tooltip
                this.qtip.reposition();
            },
            '^content.title|style.(classes|widget)$': function () {
                this.update();
            }
        };

// Extend original qTip defaults
        $.extend(TRUE, QTIP.defaults, {
            style: {
                tip: {
                    corner: TRUE,
                    mimic: FALSE,
                    width: 6,
                    height: 6,
                    border: TRUE,
                    offset: 0
                }
            }
        });
        ;
    }));
}(window, document));

+function ($) {
    'use strict';

    if ($.fn.qtip) { // if qtip enabled... set tc-design defaults
        $.fn.qtip.defaults.position.my = 'bottom center';
        $.fn.qtip.defaults.position.at = 'top center';
    }

}(window.jQuery);

+function ($) {
    'use strict';
    var show = false;
    var mobileSupport = false
    var timeout

    var ToTop = function (element, options) {
        this.options =
            this.$element = null

        this.init(element, options)
    }

    ToTop.DEFAULTS = {
        buttonText: {de: 'Nach Oben', en: 'To Top'},
        language: 'auto',
        smallButton: false,
        buttonOpacity: 0.92,
        scrollOffset: 50,
        scrollTimeMax: 800,
        scrollTimeMin: 250,
        pixelPerSecond: 4000,
        mobileBreakpoint: 639,
        dynamicVisibility: true,
        dynamicVisibilityTime: 2000,
        theme: '',
        containerType: 'container-fixed'
    }

    ToTop.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        this.options = this.mergeOptions(options)
        if (!this.options.language || this.options.language === 'auto') {
            this.options.language = this.getDocumentLanguage()
        }

        var object = this

        if ($.support.mobile === true) {
            mobileSupport = true;
            this.options.dynamicVisibility = false
        }

        if (mobileSupport === true || this.options.smallButton === true || $(window).width() < this.options.mobileBreakpoint) {
            this.generateButton(true, false)
        } else {
            this.generateButton(false, false)
        }

        this.showHideButton()

        $(window).scroll(function () {
            object.showHideButton()
        })

        $(window).resize(function () {
            var width = $(window).width()
            if (width < 639) {
                object.generateButton(true, true)
            } else if (width > 640) {
                object.generateButton(false, true)
            }
        })

        $('body').mousemove(function () {
            if (show === false) {
                object.toggleButtonVisibility(true)
                object.dynamicVisibility()
            }
        })
    }

    ToTop.prototype.mergeOptions = function (options) {
        options = options.option;
        var returnObj = {}
        var settingName
        var defaults = this.getDefaults();
        for (settingName in defaults) {
            returnObj[settingName] = defaults[settingName]
        }
        for (settingName in options) {
            returnObj[settingName] = options[settingName]
        }
        return returnObj;
    }

    ToTop.prototype.showHideButton = function () {
        if ($(window).scrollTop() > this.options.scrollOffset && show === false) {
            show = true
            $('.totop').css({
                bottom: '0px',
                opacity: this.options.buttonOpacity
            })
            this.dynamicVisibility()
        } else if ($(window).scrollTop() < this.options.scrollOffset && show === true) {
            show = false
            $('.totop').css({
                bottom: '-70px',
                opacity: 0
            })
        }
    }

    ToTop.prototype.toggleButtonVisibility = function (visible) {
        if (visible === true) {
            show = true
            $('.totop').css({opacity: this.options.buttonOpacity})
        } else if (visible === false) {
            show = false
            $('.totop').css({opacity: 0})
        }
    }

    ToTop.prototype.generateButton = function (type, replace) {
        var object = this
        var options = this.options
        var finalButton = ''
        var buttontext = this.options.buttonText[this.options.language];


        var button = '<button type="button" id="totopButton" class="btn btn-default btn-minimal">' +
            '<i class="icon icon-export" aria-hidden="true">' +
            '</i>' + buttontext + '</button>'

        if (type === true) {
            button = '<button type="button" id="totopButton" class="btn btn-default btn-icon mobile" title="' + buttontext + '">' +
                '<i class="icon icon-export" aria-hidden="true"></i>' +
                '</button>'
        }

        if (replace === true) {
            finalButton = button
            $('#totopButton').detach()
            $('#button-wrap').append(finalButton)


        } else if (replace === false) {

            finalButton = '<div class="totop">' +
                '<div class="' + options.containerType + '">' +
                '<div class="row">' +
                '<div id="button-wrap">' + button +
                '</div>' +
                '</div>' +
                '</div>' +
                '</div>'

            $('body').append(finalButton)
        }

        if (options.theme) {
            $('#totopButton').addClass(options.theme)
        }

        $('#totopButton').click(function (e) {
            e.preventDefault();

            var position = $(window).scrollTop();
            var scrollspeed = Math.round(1000 * (position / options.pixelPerSecond));
            scrollspeed = Math.min(scrollspeed, options.scrollTimeMax)
            scrollspeed = Math.max(scrollspeed, options.scrollTimeMin)

            $('html, body').stop(true, true).animate({scrollTop: 0}, scrollspeed)
            clearTimeout(timeout);
        })

        $('#totopButton').mouseenter(function (e) {
            clearTimeout(timeout);
        })

        $('#totopButton').mouseleave(function (e) {
            object.dynamicVisibility()
        })
    }

    ToTop.prototype.dynamicVisibility = function () {
        var object = this

        if (this.options.dynamicVisibility === true) {
            clearTimeout(timeout);
            timeout = setTimeout(function () {
                object.toggleButtonVisibility(false)
                clearTimeout(timeout);
            }, object.options.dynamicVisibilityTime);
        }
    }

    ToTop.prototype.getDocumentLanguage = function () {
        return $('html').attr('lang')
    }

    ToTop.prototype.getDefaults = function () {
        return ToTop.DEFAULTS
    }

    // ToTop PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        // return this.each(function () {
        var $this = $(this)
        var data = $this.data('tc.totop')
        var options = typeof option === 'object' && option
        if (!data) $this.data('tc.totop', (data = new ToTop(this, options)))
        if (typeof option === 'string') data[option].call($this)
        // })
    }

    var old = $.fn.totop

    $.fn.totop = Plugin
    $.fn.totop.Constructor = ToTop


    // ToTop NO CONFLICT
    // ====================

    $.fn.totop.noConflict = function () {
        $.fn.totop = old
        return this
    }

}(window.jQuery);

+function ($) {
    'use strict';

    var Fixation = function (element, options) {
        this.options = $.extend({}, Fixation.DEFAULTS, options)

        this.$target = $(this.options.target)
            .on('scroll.tc.fixation.data-api', $.proxy(this.checkPosition, this))

        this.$element = $(element)

        this.checkPosition()
    }

    Fixation.DEFAULTS = {
        offsetTop: 0,
        target: window
    }

    Fixation.prototype.checkPosition = function () {
        var scrollTop = this.$target.scrollTop()
        var position = this.$element.offset()
        var offsetTop = this.options.offsetTop

        if (typeof offsetTop === 'function') offsetTop = offsetTop(this.$element)

        this.$element.toggleClass('fixed', (scrollTop > (position.top - offsetTop)));
    }

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.fixation')
            var options = typeof option === 'object' && option

            if (!data) $this.data('tc.fixation', (data = new Fixation(this, options)))
            if (typeof option === 'string') data[option]()
        })
    }

    var old = $.fn.fixation

    $.fn.fixation = Plugin
    $.fn.fixation.Constructor = Fixation


    // FIXATION NO CONFLICT
    // =================

    $.fn.fixation.noConflict = function () {
        $.fn.fixation = old
        return this
    }

    $(window).on('load', function () {
        $('[data-spy="fixation"]').each(function () {
            var $spy = $(this)
            var data = $spy.data()

            Plugin.call($spy, data)
        })
    })

}(window.jQuery);


+function ($) {
    'use strict';

    var Button = function (element, options) {
        this.options =
            this.$element = null
        this.buttonGroup = false
        this.init(element, options)
    }

    Button.DEFAULTS = {}

    Button.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        if ($e.hasClass('active') === true) this.$element.attr('aria-pressed', true)
    }

    Button.prototype.setActiveStateGroup = function () {
        this.buttonGroup = true
        this.setActiveState()
    }

    Button.prototype.setActiveStateRadioGroup = function (state) {
        this.buttonGroup = true
        var $e = this.$element
        var $parrent = $e.parent()
        var eIndex = $($e).index()
        var pointer = this
        $parrent.children('button').each(function (index) {
            var $ce = $(this);
            if (index != eIndex) if (!$ce.prop('disabled') && $ce.hasClass('active')) pointer.setActiveState($ce, true)
        })
        if (!$e.hasClass('active')) this.setActiveState($e, false)
    }


    Button.prototype.setNewState = function (state) {
        var $e = this.$element
        switch (state) {
            case 'default':
                this.setActiveState($e, true)
                this.setDisableState($e, true)
                break
            case 'active':
                this.setActiveState($e, false)
                break
            case 'enable':
                this.setDisableState($e, true)
                break
            case 'disable':
                this.setDisableState($e, false)
                break
            case 'toggledisable':
                this.setDisableState()
                break
            case 'toggleactive':
                this.setActiveState()
                break
        }
    }


    Button.prototype.setDisableState = function ($e, state) {
        if ($e === undefined) $e = this.$element
        if (state === undefined) state = $e.prop('disabled')
        if (state === false) {
            $e.prop('disabled', true)
            this.setActiveState($e, true)
            this.callFunction($e, 'd')
        } else if (state === true) {
            $e.prop('disabled', false)
            this.callFunction($e, 'e')
        }
    }


    Button.prototype.setActiveState = function ($e, state) {
        if ($e === undefined) $e = this.$element
        if (state === undefined) state = $e.hasClass('active')
        this.$element.attr('aria-pressed', !state)
        if (state === false) {
            $e.addClass('active')
            this.callFunction($e, 'active')
        } else if (state === true) {
            $e.removeClass('active')
            $e.blur();
            this.callFunction($e, 'inactive')
        }
    }


    Button.prototype.callFunction = function ($e, type) {
        var callbackFilter = ''
        var callback = 'data-callback'
        var filter = 'data-callback-states'
        if ($e === undefined) $e = this.$element
        var callbackFunction = $e.attr(callback)
        if (this.buttonGroup === true) callbackFilter = $e.parent().attr(filter)
        else callbackFilter = $e.attr(filter)
        if (callbackFunction !== undefined) {
            if (type === 'press') window[callbackFunction]($e, type) // window[callbackFunction].call($e, type)
            else if (callbackFilter === 'all') window[callbackFunction]($e, type) // window[callbackFunction].call($e, type)
        }
    }


    // Button PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.button')
            var options = typeof option === 'object' && option
            if (!data) $this.data('tc.button', (data = new Button(this, options)))
            if (option == 'toggleActiveState') data.setActiveState()
            if (option == 'toggleActiveStateRadioGroup') data.setActiveStateRadioGroup(false)
            if (option == 'toggleActiveStateGroup') data.setActiveStateGroup()
            if (option == 'forwardPressState') data.callFunction(undefined, 'press')
            else if (option) data.setNewState(option)
            /* if (typeof option == 'string') data[option].call($this) */
        })
    }

    var old = $.fn.button

    $.fn.button = Plugin
    $.fn.button.Constructor = Button

    // Button API
    // ====================

    $(document).on('click.tc.button.data-api', '[data-toggle="button"]', function (e) {
        var $e = $(e.target)
        Plugin.call($e, 'toggleActiveState')
        Plugin.call($e, 'forwardPressState')
    })

    $(document).on('click.tc.button.data-api', '[data-toggle="buttongroup-radio"]', function (e) {
        var $e = $(e.target)
        Plugin.call($e, 'toggleActiveStateRadioGroup')
        Plugin.call($e, 'forwardPressState')
    })

    $(document).on('click.tc.button.data-api', '[data-toggle="buttongroup"]', function (e) {
        var $e = $(e.target)
        Plugin.call($e, 'toggleActiveStateGroup')
        Plugin.call($e, 'forwardPressState')
    })

    // Button NO CONFLICT
    // ====================

    $.fn.button.noConflict = function () {
        $.fn.button = old
        return this
    }

}(window.jQuery);

+function ($) {
    'use strict';
    var id = 0;

    var Expandable = function (element, options) {
        this.options =
            this.$element = $(element)
        this.init(element, options)
    }

    Expandable.DEFAULTS = {
        speed: 400,
        syncToSize: false,
        state: true,
        height: 0,
        group: undefined,
        id: ''
    }

    Expandable.prototype.init = function (element, options) {
        var $e = this.$element = $(element)
        this.options = this.mergeOptions(options)
        var getOptions = this.options
        if (getOptions.group) getOptions.id = getOptions.group + '-' + id
        else getOptions.id = id
        id++
        if ($e.hasClass('expandable-hidden') === true) {
            $($e).wrap('<div id="' + getOptions.id + '" class="expanable-container expandable-hidden"></div>')
            $($e).parent().css({height: 0, opacity: 0})
            $($e).removeClass('expandable-hidden')
            getOptions.state = false
        } else {
            $($e).wrap('<div id="' + getOptions.id + '" class="expanable-container"></div>')
            getOptions.state = true
        }
        this.calculateHeight()
        var object = this
    }

    Expandable.prototype.calculateHeight = function () {
        var element = this.$element
        var option = this.options

        var copy = $(element).clone().attr('id', false)
            .css({
                display: 'inline-block',
                position: 'absolute',
                left: '-10000px',
                width: $(element).parent().innerWidth()
            });
        $('body').append(copy);
        option.height = copy.outerHeight(true)
        copy.remove()
    }


    Expandable.prototype.visible = function () {
        var data = this.data('tc.expandable')
        var options = data.options
        data.setVisibility(this, false)
    }


    Expandable.prototype.hidden = function () {
        var data = this.data('tc.expandable')
        var options = data.options
        data.setVisibility(this, true)
    }


    Expandable.prototype.toggleVisibility = function () {
        var data = this.data('tc.expandable')
        var options = data.options
        var eIndex = $(options.group).index()

        if (options.group !== undefined) {
            $('.' + options.group).each(function (index) {
                if (options.id != $(this).data('tc.expandable').options.id) {
                    data.setVisibility($(this), true)
                }
            })
            data.setVisibility(this, false)
        } else {
            data.setVisibility(this)

        }
    }


    Expandable.prototype.setVisibility = function ($e, state) {
        var data = $e.data('tc.expandable')
        var options = data.options
        var speed = 0
        var $parent = $($e).parent()
        if (options.syncToSize === true) speed = 1000 / options.speed * options.height
        else speed = options.speed

        if (state === undefined) state = options.state

        if (state === true) {
            $parent.stop(true, false).animate({height: 0, opacity: 0}, speed, function () {
                $parent.addClass('expandable-hidden')
            })
            options.state = false
        } else if (state === false) {
            $parent.removeClass('expandable-hidden')
            $parent.stop(true, false).animate({height: options.height + 'px', opacity: 1}, speed)
            options.state = true
        }

    }


    Expandable.prototype.getDefaults = function () {
        return Expandable.DEFAULTS
    }


    Expandable.prototype.mergeOptions = function (options) {
        options = options.option;
        var returnObj = {}
        var settingName
        var defaults = this.getDefaults();
        for (settingName in defaults) {
            returnObj[settingName] = defaults[settingName]
        }
        for (settingName in options) {
            returnObj[settingName] = options[settingName]
        }
        return returnObj;
    }

    // Expandable PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {
            var $this = $(this)
            var data = $this.data('tc.expandable')
            var options = typeof option === 'object' && option
            if (!data) $this.data('tc.expandable', (data = new Expandable(this, options)))
            else if (typeof option === 'string') data[option].call($this)
        })
    }

    var old = $.fn.expandable

    $.fn.expandable = Plugin
    $.fn.expandable.Constructor = Expandable

    // expanable NO CONFLICT
    // ====================

    $.fn.expandable.noConflict = function () {
        $.fn.expandable = old
        return this
    }

}(window.jQuery);
