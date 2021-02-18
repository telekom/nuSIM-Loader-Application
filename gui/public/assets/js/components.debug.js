/*!
 * Telekom Components v2.2.4 [2018-04-19]
 * Copyright 2014-2018 Deutsche Telekom AG
 */

if (typeof jQuery === 'undefined') {
    throw new Error('jQuery is required')
}

+function ($) {
    'use strict';

    var DebugThingy = function (element, options) {
        this.options = this.$element = null;
        this.init(element, options);
    };

    DebugThingy.DEFAULTS = {
        template:
        '<div id="debug-thingy" class="noselect">' +
        '  <i class="icon icon-cancel" id="debug-thingy-btnHide"></i>' +
        '  <div class="size-o-tron">' +
        '    <div class="size-bar">&nbsp;</div>' +
        '    <div class="breakpoint bp-s">&nbsp;</div>' +
        '    <div class="breakpoint bp-m">&nbsp;</div>' +
        '    <div class="breakpoint bp-l">&nbsp;</div>' +
        '    <div class="breakpoint bp-xl">&nbsp;</div>' +
        '    <div class="text-right"><span class="size-info"></span></div>' +
        '  </div>' +
        '  <div class="col-xs-hide text-nowrap">' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-imageOverlayToggleOpenSettings" title="Overlay Settings"><i class="fa fa-image" aria-hidden="true"></i></button>' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleGridVisibility" title="Grid visibility"><i class="fa fa-columns" aria-hidden="true"></i></button>' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleCrossSectionVisibility" title="Grid building units pattern"><i class="fa fa-delicious" aria-hidden="true"></i></button>' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleBreakpoints" title="Show breakpoints"><i class="fa fa-caret-square-o-down" aria-hidden="true"></i></button>' +
        '  </div>' +

        '  <div class="col-xs-hide text-nowrap">' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-colorPicker" title="Find Color"><i class="fa fa-eyedropper"></i></button>' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-measureRect" title="Measuring Rectangle"><i class="fa fa-expand"></i></button>' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleStyleSheets" title="Toggle CSS">CSS</button>' +
        '  </div>' +
        '  <div class="col-xs-hide text-nowrap">' +
        '    <span id="debug-thingy-pipetteInfo">' +
        '      <span id="debug-thingy-pipetteInfo-colorSwatch" class="color-swatch"><i class="fa fa-font"></i></span>&ensp;' +
        '      <span id="debug-thingy-pipetteInfo-colorValue"/><br>' +
        '      <span id="debug-thingy-pipetteInfo-backgroundColorValue"/><br>' +
        '      <span id="debug-thingy-pipetteInfo-borderColorValue"/><br>' +
        '    </span>' +
        '  </div>' +

        '  <div class="col-xs-hide text-nowrap text-right">' +
        '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-togglePageLock" title="(Un)Lock row heights"><i class="icon icon-lock" aria-hidden="true"></i></button>' +
        '  </div>' +
//            '  <div class="col-xs-hide text-nowrap debug-thingy-demo-buttons">' +
//            '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleDemoGrids"><i class="icon icon-list-view" aria-hidden="true"></i></button>' +
//            '    <button class="btn btn-default btn-small btn-minimal btn-icon" id="debug-thingy-toggleCrossSectionDemoGrids"><i class="icon icon-tile-view" aria-hidden="true"></i></button>' +
//            '  </div>' +

        '    <div class="debug-col text-nowrap">' +
        '      <span id="debug-thingy-positionInfoValue"/>' +
        '    </div>' +

        '    <div class="debug-col text-nowrap">' +
        '      <span id="measure-rect-info">' +
        '       x: <span id="measure-rect-info-x"/><br>' +
        '       y: <span id="measure-rect-info-y"/><br>' +
        '       width: <span id="measure-rect-info-width"/><br>' +
        '       height: <span id="measure-rect-info-height"/><br>' +
        '      </span>' +
        '    </div>' +


        '    <div class="debug-col text-nowrap">' +
        '      Zoom <input id="debug-thingy-pageZoomSlider" type="range" min=".25" max="4" step=".25" value="1"> <span id="debug-thingy-pageZoomSliderValue">100 %</span>' +
        '    </div>' +
        // image overlay settings

        '  <div class="debug-image-overlay-settings col-xs-hide hidden">' +
        '    <div class="text-nowrap">' +
        '      <button class="btn btn-small btn-minimal btn-icon" id="debug-thingy-imageOverlayToggleActivity" title="Toggle visibility"><i class="fa fa-eye-slash" aria-hidden="true"></i></button>' +
        '      <button class="btn btn-small btn-minimal btn-icon" id="debug-thingy-imageOverlayToggleAlignment" title="Overlay alignment"><i class="fa fa-align-left" aria-hidden="true"></i></button>' +
        '      <button class="btn btn-small btn-minimal btn-icon" id="debug-thingy-imageOverlayToggleStyle" title="Invert overlay"><i class="fa fa-plus-square-o" aria-hidden="true"></i></button>' +
        '      <button class="btn btn-small btn-minimal btn-icon" id="debug-thingy-imageOverlayToggleFixed" title="Position absolute/fixed"><i class="fa fa-paperclip" aria-hidden="true"></i></button>' +
        '    </div>' +
        '    <div class="debug-col text-nowrap">' +
        '    <br>' +
        '      x <input type="number" id="debug-thingy-transformX">' +
        '      y <input type="number" id="debug-thingy-transformY">' +
        '      scale <input type="number"  id="debug-thingy-transformScale" min="0.5" max="5" step=".5">' +
        '    </div>' +
        '    <div class="debug-col text-nowrap">' +
        '      &alpha; <input id="debug-thingy-imageOverlayAlphaSlider" type="range" min="0" max="100"> <span id="debug-thingy-imageOverlayAlphaSliderValue"/>' +
        '    </div>' +
        '    <br>' +
        '    <div class="debug-col"><span class="icon" title="Open overlay Image"><i class="fa fa-laptop" aria-hidden="true"></i></span><input type="file" id="debug-thingy-image-overlay-canvas" accept="image/*"><i class="fa fa-times-circle" aria-hidden="true" style="float:right;" id="debug-thingy-image-overlay-canvas-delete"></i></div>' +
        '    <br>' +
        '    <div class="debug-col"><span class="icon" title="Image path"><i class="fa fa-globe" aria-hidden="true"></i></span><input type="text" id="debug-thingy-image-overlay-path" /></div>' +
        '    <div class="debug-col"><span class="icon" title="Filename"><i class="fa fa-file-image-o" aria-hidden="true"></i></span><input type="text" id="debug-thingy-image-overlay-filename" /></div>' +
        '    <div class="debug-col"><span class="icon" title="Open overlay Image"></span><input type="file" id="debug-thingy-image-overlay" accept="image/*"></div>' +
        '    <br>' +
        '    <div class="debug-col"><span class="icon" title="Additional CSS"><strong>{}</strong></span><input type="text" id="debug-thingy-imageOverlayCustomCSS"/></div>' + '  </div>' +
        '</div>'
    };

    DebugThingy.prototype.settings = {};

    var $frame;
    var $scrollPane;
    var screenSize = {x: 20, y: 80, width: 800, height: 600};
    var debugScrollPaneThumbSize = 20;

    DebugThingy.prototype.init = function (element, options) {

        function updateScreenSize() {

            if (mouseInfoObject && mouseInfoObject.dragObject) {
                if ($(mouseInfoObject.dragObject).hasClass('horizontal'))
                    screenSize.width += mouseInfoObject.mousePositionDelta.x / zoom;
                if ($(mouseInfoObject.dragObject).hasClass('vertical'))
                    screenSize.height += mouseInfoObject.mousePositionDelta.y / zoom;
            }

            $scrollPane.css({
                top: screenSize.y,
                left: screenSize.x,
                width: screenSize.width * zoom,
                height: screenSize.height * zoom,
                'max-width': $(window).innerWidth() - screenSize.x - debugScrollPaneThumbSize,
                'max-height': $(window).innerHeight() - screenSize.y - debugScrollPaneThumbSize
            });
//        $frame.css({width: screenSize.width * zoom});
            $('.debug-scroll-pane-thumb.horizontal').css({
                top: screenSize.y + $scrollPane.height() * 0.5 - debugScrollPaneThumbSize * 0.5,
                left: screenSize.x + $scrollPane.width()
            });
            $('.debug-scroll-pane-thumb.vertical').css({
                top: screenSize.y + $scrollPane.height(),
                left: screenSize.x + $scrollPane.width() * 0.5 - debugScrollPaneThumbSize * 0.5
            });

            var props = {};
            if (zoom === 1) {
                props = {
                    'transform-origin': '',
                    transform: '',
                    width: '',
                    height: ''
                };
            } else {
                props = {
                    'transform-origin': 'top left',
                    transform: 'scale(' + zoom + ',' + zoom + ')',
                    width: screenSize.width,
                    height: Math.min((($('.debug-scroll-pane').innerHeight() - 20) / zoom), screenSize.height)
                };
            }

            $frame.css(props);

            setGridTronBreakPointPositions();

            sizeInfo();
            // applyImageOverlayStyle();
        }


        $scrollPane = $('<div class="debug-scroll-pane" />').appendTo('body');

        $frame = $('<iframe class="debug-iframe" src="' + window.location + '" />').appendTo($scrollPane);

        // var doc = $frame[0].contentDocument || $frame[0].contentWindow.document;
        // doc.open();
        // doc.write('<!DOCTYPE html><html><head><title></title></head><body></body></html>');
        // doc.close();
        $('head').children().appendTo($frame.contents().find('head'));
        $('body').children().not($scrollPane).appendTo($frame.contents().find('body'));


        function updateContentWindowSize() {
            updateScreenSize();
            applyImageOverlayStyle();
        }

        function debugScrollPaneThumbMouseDownHandler(e) {
            mouseInfoObject.mousePosition = {x: Math.round(e.pageX), y: Math.round(e.pageY)};
            startDrag(e.currentTarget, updateContentWindowSize);
        }

        $('body').append('<div class="debug-scroll-pane-thumb horizontal" /><div class="debug-scroll-pane-thumb vertical" />');
        $('.debug-scroll-pane-thumb').mousedown(this, debugScrollPaneThumbMouseDownHandler);


        updateScreenSize();


        var SETTINGS;
        var pageLock;
        var pageLockDimensions;
        var reopen;

        var $e = this.$element = $(element);
        this.options = this.getOptions(options);
        var $debugThingy = this.debugThingy();

        $('body').append($(this.options.template));

        function $fContent() {
            return $($frame[0].contentDocument);
        }

        function getComponentStyleSheet() {
            var version = '2.2.4';
            var sheets = $(document.styleSheets);
            for (var i = sheets.length - 1; i >= 0; --i) {
                var sheet = sheets[i];
                var r = sheet.rules || sheet.cssRules;
                if (r && r[0] && r[0].selectorText === '.t-html-cc-ver') {
                    if (r[0].style && r[0].style.content && r[0].style.content.match(/Telekom HTML Code Components v.*/)) {
                        if (r[0].style.content.match(new RegExp('Telekom HTML Code Components v' + version))) {
                            return sheet;
                        } else {
                            return null;
                        }
                    }
                }
            }
            return null;
        }

        function getBreakpoints(styleSheets) {
            var defaultBreakpoints = [320, 640, 1024, 1500];
            var breakpoints = [];
            var tolerance = 4;
            if (!styleSheets) {
                console.warn('debugThingy: styleSheets not accessible - using default breakpoints');
                breakpoints = defaultBreakpoints;
            } else {
                var rules = styleSheets.cssRules || styleSheets.rules;
                for (var j = 0; j < rules.length; ++j) {
//          if (rules[j].selectorText) {
                    if (!rules[j].media || !rules[j].media.mediaText)
                        continue;

                    var media = rules[j].media.mediaText;

                    var breakpoint = Math.round(Number(media.match(/.*width:\s*(\d+)px/)[1]) / tolerance) * tolerance;
                    if (breakpoints.indexOf(breakpoint) === -1)
                        breakpoints.push(breakpoint);

//          }
                }
                breakpoints = breakpoints.sort(function (a, b) {
                    return a - b;
                });
                if (breakpoints.length === 0) {
                    // hardcoded bp for ie8 etc.
                    console.warn('debugThingy: media rules not found - using default breakpoints');
                    breakpoints = defaultBreakpoints;
                }
//        console.log('debugThingy Breakpoints:', breakpoints)
            }
            breakpoints.unshift(0);

            return breakpoints;
        }

        function toggleBreakpointsOnResize(e) {
            DebugThingy.prototype.settings.breakpoints = !DebugThingy.prototype.settings.breakpoints;
            sizeInfo();
            updateButtonStates();
            saveSettings();
        }

        function toggleGridVisibility(e) {
            SETTINGS.gridOverlay = !SETTINGS.gridOverlay;
            applyGridVisibility();
            saveSettings();
        }

        function applyGridVisibility() {
            if (SETTINGS.gridOverlay) {
                $fContent().find('body').addClass('grid-debug');
            } else {
                $fContent().find('body').removeClass('grid-debug');
            }
            updateButtonStates();
        }

        function toggleCrossSectionGrid(e) {
            SETTINGS.csGrid = !SETTINGS.csGrid && !SETTINGS.csOverlay;
            SETTINGS.csOverlay = false;
            applyCrossSectionGrid();
            applyCrossSectionOverlay();
            saveSettings();
        }

        function applyCrossSectionGrid() {
            $('.debug-bu-overlay').remove('');
            if (SETTINGS.csGrid) {
                $fContent().find('body').addClass('debug-cross-section');
            } else {
                $fContent().find('body').removeClass('debug-cross-section');
            }
            updateButtonStates();
        }

        function toggleCrossSectionOverlay(e) {
            SETTINGS.csOverlay = !SETTINGS.csOverlay;
            SETTINGS.csGrid = false;
            applyCrossSectionGrid();
            applyCrossSectionOverlay();
            saveSettings();
        }

        function applyCrossSectionOverlay(e) {
            if (SETTINGS.csOverlay) {
                $('<div class="debug-bu-overlay" />').css({height: $fContent().find('body').height()}).appendTo($fContent().find('body'));
                $('body').removeClass('debug-cross-section');
            } else {
                $fContent().find('.debug-bu-overlay').remove();
            }
            updateButtonStates();
        }

        function displayImageData(imgData) {
            if (imgData !== null) {
                var image = $('<img/>').attr('src', imgData);
                $('.debug-image-overlay-wrapper').remove();
                $('body').append('<div class="debug-image-overlay-wrapper"><div class="debug-image-overlay"></div></div>');
                $('.debug-image-overlay').append(image);
                applyImageOverlayStyle();
            }
        }

        function readFileData(imgData) {
            $('.debug-image-overlay img').remove();
            displayImageData(imgData);
            window.sessionStorage.setItem('debug-image-overlay-file-data', imgData);
        }

        function readFileFromStorage() {
            var imgData = window.sessionStorage.getItem('debug-image-overlay-file-data');
            if (imgData) {
                displayImageData(imgData);
                return true;
            }
            return false;
        }

        function readFile(input) {
            if (input && input.files) {
                $('.debug-image-overlay img').remove();
                if (input.files[0]) {
                    var file = input.files[0];
                    if ((/image/i).test(file.type)) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            readFileData(e.target.result)
                        };
                        reader.readAsDataURL(file);
                    } else {
                        alert('Not an image file.');
                        $('.debug-image-overlay').remove();
                    }
                }
            }
        }

        function readURL(input) {
            var fileName = window.sessionStorage.getItem('debug-thingy-image-overlay-file-name');
            var defaultFile = '';
            var defaultPath = 'images/';
            if (!fileName) {
                fileName = defaultFile;
            }
            if (input && input.files) {
                if (input.files[0]) {
                    fileName = input.files[0].name;
                    input.files = null;
                } else if ($('#debug-thingy-image-overlay-filename').val()) {
                    fileName = $('#debug-thingy-image-overlay-filename').val();
                } else {
                    fileName = defaultFile;
                }
            }
            $('#debug-thingy-image-overlay-filename').val(fileName);
            window.sessionStorage.setItem('debug-thingy-image-overlay-file-name', fileName);
            if (fileName) {
                $('.debug-image-overlay').remove();
                $('body').append('<div class="debug-image-overlay"></div>');
                if (fileName === defaultFile) {
                    $('.debug-image-overlay').css({
                        'background-image': 'url("' + defaultPath + defaultFile + '")',
                        'background-repeat': 'repeat'
                    });
                } else {
                    $('.debug-image-overlay').css({
                        background: 'url("' +
                        $('#debug-thingy-image-overlay-path').val() +
                        fileName +
                        '")', 'background-repeat': 'no-repeat'
                    });
                }
                applyImageOverlayStyle()
            }
        }

        function imageOverlayToggleOpenSettings(e) {
            SETTINGS.overlayImage.open = !SETTINGS.overlayImage.open;
            applyImageOverlayOpen();
            saveSettings();
        }

        function applyImageOverlayOpen() {
            if (SETTINGS.overlayImage.open) {
                $('.debug-image-overlay-settings').removeClass('hidden');
            } else {
                $('.debug-image-overlay-settings').addClass('hidden');
            }
            updateButtonStates();
        }

        function imageOverlayToggleActivity(e) {
            SETTINGS.overlayImage.active = !SETTINGS.overlayImage.active;
            applyImageOverlayActivity();
            saveSettings();
        }

        function applyImageOverlayActivity() {
            var active = SETTINGS.overlayImage.active;
            $('.debug-image-overlay').remove();
            if (active) {
                var result = readFileFromStorage();
                if (!result)
                    readURL();
            }
            var $ico = $('#debug-thingy-imageOverlayToggleActivity > i');
            $ico.addClass(!active ? 'fa-eye-slash' : 'fa-eye');
            $ico.removeClass(active ? 'fa-eye-slash' : 'fa-eye');
            updateButtonStates();
        }

        function applyImageOverlayAlignment() {
            $('.debug-image-overlay').removeClass('align-left');
            $('.debug-image-overlay').removeClass('align-center');
            $('.debug-image-overlay').removeClass('align-right');
            $('#debug-thingy-imageOverlayToggleAlignment > i').removeClass('fa-align-left');
            $('#debug-thingy-imageOverlayToggleAlignment > i').removeClass('fa-align-center');
            $('#debug-thingy-imageOverlayToggleAlignment > i').removeClass('fa-align-right');

            $('.debug-image-overlay').addClass('align-' + SETTINGS.overlayImage.alignment);
            $('#debug-thingy-imageOverlayToggleAlignment > i').addClass('fa-align-' + SETTINGS.overlayImage.alignment);
            updateButtonStates();
        }

        function imageOverlayToggleAlignment(e) {
            var arr = ['left', 'center', 'right'];
            SETTINGS.overlayImage.alignment = arr[(arr.indexOf(SETTINGS.overlayImage.alignment) + 1) % arr.length];
            saveSettings();
            applyImageOverlayStyle(); // scaled images will need other orientation
            saveSettings();
        }

        function toggleDemoGrids(e) {
            $(e.currentTarget).toggleClass('btn-positive');
            $('.grid-debug, .grid-debug-disabled').each(function (key, val) {
                if (val.nodeName !== 'BODY') {
                    $(val).toggleClass('grid-debug-disabled');
                    $(val).toggleClass('grid-debug');
                } else {
                    $(val).toggleClass('grid-debug');
                }
            });
            updateButtonStates();
            saveSettings();
        }

        function toggleCrossSectionDemoGrids(e) {
            $(e.currentTarget).toggleClass('btn-positive');
            $('body.debug-cross-section').removeClass('debug-cross-section');
            $('.grid-debug, .grid-debug-disabled').each(function (key, val) {
                if (val.nodeName !== 'BODY') {
                    $(val).toggleClass('debug-cross-section');
                }
            });
            updateButtonStates();
            saveSettings();
        }

        function togglePageLockHandler(e) {
            if ((!window.selfie || window.selfie.closed) && (reopen === undefined || e.shiftKey)) {
                reopen = confirm('Do you want to open this page in a new window?\n\nThis will enable the lock button to restore the window dimensions/position on unlocking.');
                if (reopen) {
                    window.selfie = window.open(window.location.pathname, '', 'menubar=yes, toolbar=yes, location=yes, personalbar=yes, status=yes, resizable=yes, width=' + window.outerWidth + ', height=' + window.outerHeight);
                    window.selfie.selfie = window.selfie;
                } else {
                    togglePageLock(e);
                }
            } else {
                togglePageLock(e);
            }
        }

        function togglePageLock(e) {

            pageLock = !pageLock;

            $(e.currentTarget).toggleClass('btn-negative');
//      $('body')[0].style.cssText =
//              pageLock ?
//                'width:' + ($(window).width()) + 'px;' +
//                'min-width:' + ($(window).width()) + 'px;' :
//                '100%';
            $('.row')
            // .not('.demo-grid')
                .each(function (key, val) {
                    if (pageLock) {
                        $(val).css({
                            // width: $(val).outerWidth(),
                            height: $(val).outerHeight(),
                            'max-height': $(val).outerHeight(),
                            overflow: 'hidden'
                        });
                    } else {
                        $(val).attr('style', '');
                        demoGridWrapperResizeHandler();
                    }
                });

            if (pageLock) {
                pageLockDimensions = {
                    w: window.outerWidth || $(window).width(),
                    h: window.outerHeight || $(window).height(),
                    t: $(window).scrollTop(),
                    l: $(window).scrollLeft()
                };
            } else if (pageLockDimensions) {
                if (window.selfie) {
                    window.resizeTo(pageLockDimensions.w, pageLockDimensions.h);
                    $(window).scrollTop(pageLockDimensions.t);
                    $(window).scrollLeft(pageLockDimensions.l);
                }
            }
        }

        function updateButtonStates() {

            if (SETTINGS.overlayImage.open) {
                $('#debug-thingy-imageOverlayToggleOpenSettings').addClass('btn-positive');
            } else {
                $('#debug-thingy-imageOverlayToggleOpenSettings').removeClass('btn-positive');
            }

            if (SETTINGS.gridOverlay) {
                $('#debug-thingy-toggleGridVisibility').addClass('btn-positive');
            } else {
                $('#debug-thingy-toggleGridVisibility').removeClass('btn-positive');
            }

            var active = $('.grid-debug-disabled').length === 0;
            if (active) {
                $('#debug-thingy-toggleDemoGrids').addClass('btn-positive');
            } else {
                $('#debug-thingy-toggleDemoGrids').removeClass('btn-positive');
            }

            if (SETTINGS.csGrid) {
                $('#debug-thingy-toggleCrossSectionVisibility').addClass('btn-positive');
            } else {
                $('#debug-thingy-toggleCrossSectionVisibility').removeClass('btn-positive');
            }

            if (SETTINGS.csOverlay) {
                $('#debug-thingy-toggleCrossSectionVisibility').addClass('btn-brand');
            } else {
                $('#debug-thingy-toggleCrossSectionVisibility').removeClass('btn-brand');
            }

            active = $('.debug-cross-section:not(body)').length > 0;
            if (active) {
                $('#debug-thingy-toggleCrossSectionDemoGrids').removeClass('btn-positive');
            } else {
                $('#debug-thingy-toggleCrossSectionDemoGrids').addClass('btn-positive');
            }

            if (!SETTINGS.breakpoints) {
                $('#debug-thingy-toggleBreakpoints').removeClass('btn-positive');
                $('.debug-breakpoint-thingy').finish().hide();
            } else {
                $('#debug-thingy-toggleBreakpoints').addClass('btn-positive');
            }
        }

        function processSettings() {
            if (SETTINGS.open === false)
                $('#debug-thingy').addClass('hidden');
            if (SETTINGS.gridOverlay)
                applyGridVisibility();
            if (SETTINGS.csGrid)
                applyCrossSectionGrid();
            if (SETTINGS.csOverlay)
                applyCrossSectionOverlay();
            if (SETTINGS.breakpoints)
                sizeInfo();

            applyImageOverlayOpen();
            applyImageOverlayActivity();
            applyImageOverlayStyle();
            updateButtonStates();
        }

        function loadSettings() {
            SETTINGS = DebugThingy.prototype.settings = JSON.parse(window.sessionStorage.getItem('debug-thingy-settings')) ||
                {
                    open: true,
                    gridOverlay: false,
                    csGrid: false,
                    csOverlay: false,
                    breakpoints: true,
                    overlayImage: {
                        open: false,
                        active: false,
                        alignment: 'left',
                        fixed: false,
                        style: 'styleA',
                        transform: {
                            x: 0,
                            y: 0,
                            scale: 1
                        },
                        styleA: {
                            alpha: 0.25,
                            css: {}
                        },
                        styleB: {
                            alpha: 1,
                            css: {}
                        }
                    }
                };

            processSettings();
        }

        function saveSettings() {
            window.sessionStorage.setItem('debug-thingy-settings', JSON.stringify(DebugThingy.prototype.settings = SETTINGS));
        }

        function imageOverlayToggleStyle() {
            var arr = ['styleA', 'styleB'];
            SETTINGS.overlayImage.style = arr[(arr.indexOf(SETTINGS.overlayImage.style) + 1) % arr.length];
            applyImageOverlayStyle();
            saveSettings();
        }

        function iframeScrollHandler() {
            applyImageOverlayStyle();
        }

        function applyImageOverlayStyle() {

            if (!SETTINGS || !SETTINGS.overlayImage)
                return;

            var currentStyle = SETTINGS.overlayImage[SETTINGS.overlayImage.style];
            var isStyleB = SETTINGS.overlayImage.style == 'styleB';

            // update button icon
            var $ico = $('#debug-thingy-imageOverlayToggleStyle > i');
            $ico.removeClass(isStyleB ? 'fa-plus-square-o' : 'fa-minus-square');
            $ico.addClass(!isStyleB ? 'fa-plus-square-o' : 'fa-minus-square');

            // set supported negative filter
            if (isStyleB) {
                var supportsMixBlendMode = false;
                if ('CSS' in window && 'supports' in window.CSS) {
                    supportsMixBlendMode = window.CSS.supports('mix-blend-mode', 'multiply');
                }
                $('.debug-image-overlay').addClass(supportsMixBlendMode ? 'mixed' : 'inverted');
            } else {
                $('.debug-image-overlay').removeClass('mixed');
                $('.debug-image-overlay').removeClass('inverted');
            }

            // update custom CSS text input
            $('#debug-thingy-imageOverlayCustomCSS').val(currentStyle.customCSS);
            applyImageOverlayCustomCSS();

            // quick styles
            applyImageOverlayAlignment();
            applyImageOverlayFixed();

            var trans = SETTINGS.overlayImage.transform;
            $('#debug-thingy-transformX').val(trans.x || 0);
            $('#debug-thingy-transformY').val(trans.y || 0);
            $('#debug-thingy-transformScale').val(trans.scale || 1);

            var transStr = 'translate(' +
                (Number(trans.x) + screenSize.x - (SETTINGS.overlayImage.fixed ? 0 : $fContent().scrollLeft() * zoom) - $('.debug-scroll-pane').scrollLeft()) + 'px,' +
                (Number(trans.y) + screenSize.y - (SETTINGS.overlayImage.fixed ? 0 : $fContent().scrollTop() * zoom)) + 'px) ' +
                'scale(' +
                trans.scale * zoom + ',' + trans.scale * zoom +
                ')';
//      console.log(transStr)
            $('.debug-image-overlay img').css({
                'transform-origin': 'top ' + SETTINGS.overlayImage.alignment,
                transform: transStr
            });

            // set alpha
            var opacity = SETTINGS.overlayImage[SETTINGS.overlayImage.style].alpha;
            $('.debug-image-overlay').css('opacity', opacity);
            $('#debug-thingy-imageOverlayAlphaSliderValue').text(Math.round(SETTINGS.overlayImage[SETTINGS.overlayImage.style].alpha * 100) + ' %');
            $('#debug-thingy-imageOverlayAlphaSlider').val(SETTINGS.overlayImage[SETTINGS.overlayImage.style].alpha * 100);

            $('.debug-image-overlay-wrapper').css({width: screenSize.width, height: screenSize.height});
            $('.debug-image-overlay').css('clip', 'rect(' + screenSize.y + 'px,' + ($frame.outerWidth() * zoom + screenSize.x) + 'px,' + ($frame.outerHeight() * zoom + screenSize.y) + 'px,' + screenSize.x + 'px)');
        }

        function imageOverlayCustomCSSChange() {
            var currentStyle = SETTINGS.overlayImage[SETTINGS.overlayImage.style];
            currentStyle.customCSS = $('#debug-thingy-imageOverlayCustomCSS').val();
            saveSettings();
            applyImageOverlayStyle();
        }

        function applyImageOverlayCustomCSS() {
            var backgroundImage = $('.debug-image-overlay').css('background-image');
            var backgroundRepeat = $('.debug-image-overlay').css('background-repeat');

            var customStyle = {};
            try {
                var styleStr = SETTINGS.overlayImage[SETTINGS.overlayImage.style].customCSS;
                styleStr = styleStr.replace(/(.*?):(.*?)(;|$)/g, '"$1":"$2"').split('""').join('","');
                customStyle = JSON.parse('{' + styleStr + '}');
            } catch (e) {
            }

            var style = customStyle;
            if (backgroundImage)
                style['background-image'] = backgroundImage;
            if (backgroundRepeat)
                style['background-repeat'] = backgroundRepeat;

            $('.debug-image-overlay').attr('style', null);

            var height = Math.max(document.body.scrollHeight, document.body.offsetHeight, document.body.clientHeight, document.documentElement.scrollHeight, document.documentElement.offsetHeight);
            $('.debug-image-overlay').css({'min-height': height});

            $('.debug-image-overlay').css(style);
        }

        function imageOverlayToggleFixed() {
            SETTINGS.overlayImage.fixed = !SETTINGS.overlayImage.fixed;
            applyImageOverlayStyle();
            saveSettings();
        }

        function applyImageOverlayFixed() {
            var isFixed = SETTINGS.overlayImage.fixed;
//      $('.debug-image-overlay').css({'position': isFixed ? 'fixed' : 'absolute'});
            var $ico = $('#debug-thingy-imageOverlayToggleFixed > i');
            $ico.removeClass(isFixed ? 'fa-paperclip' : 'fa-thumb-tack');
            $ico.addClass(!isFixed ? 'fa-paperclip' : 'fa-thumb-tack');
        }

        function imageOverlayAlphaChange(e) {
            SETTINGS.overlayImage[SETTINGS.overlayImage.style].alpha = $(e.currentTarget).val() / 100;
            applyImageOverlayStyle();
            if (e.type === 'change')
                saveSettings();
        }

        var numberInputMultiplier = 1;

        function imageOverlayTransformChange(e) {

            if ((e.type === 'keyup' || e.type === 'keydown') && $(e.currentTarget)[0] != $('input#debug-thingy-transformScale')[0]) {
                numberInputMultiplier = e.shiftKey ? 10 : 1;

                if (e.type === 'keydown') {
                    if (e.keyCode === 38) {
                        e.preventDefault();
                        $(e.currentTarget).val(Number($(e.currentTarget).val()) + numberInputMultiplier);
                    }
                    if (e.keyCode === 40) {
                        e.preventDefault();
                        $(e.currentTarget).val(Number($(e.currentTarget).val()) - numberInputMultiplier);
                    }
                }
            }

            var x = $('#debug-thingy-transformX').val() || 0;
            var y = $('#debug-thingy-transformY').val() || 0;
            var scale = $('#debug-thingy-transformScale').val() || 1;
            var trans = SETTINGS.overlayImage.transform;
            if (x === trans.x && y === trans.y && scale === trans.scale)
                return;

            SETTINGS.overlayImage.transform = {x: x, y: y, scale: scale};
            applyImageOverlayStyle();
            saveSettings();
        }

        var zoom = 1;
        var internalZoom = 1;

        function pageZoomSliderChange(e) {
            zoom = Number($(e.currentTarget).val());
            $('#debug-thingy-pageZoomSliderValue').text(zoom * 100 + ' %');

            updateScreenSize();

            // $('.debug-image-overlay').css(props);

            if (measureRectActive)
                updateMeasureRect();
            applyImageOverlayStyle();
        }

        function pageZoomSliderInput(e) {
            var zoom = Number($(e.currentTarget).val());
            $('#debug-thingy-pageZoomSliderValue').text(zoom * 100 + ' %');
        }

        function mouseMoveHandler(e) {
            mouseInfoObject.currentMousePosition.x = e.pageX;
            mouseInfoObject.currentMousePosition.y = e.pageY;
            mouseInfoObject.mousePositionDelta.x = mouseInfoObject.currentMousePosition.x - mouseInfoObject.mousePosition.x;
            mouseInfoObject.mousePositionDelta.y = mouseInfoObject.currentMousePosition.y - mouseInfoObject.mousePosition.y;

            if (mouseInfoObject.dragObject) {
                mouseInfoObject.dragObject.x += mouseInfoObject.mousePositionDelta.x;
                mouseInfoObject.dragObject.y += mouseInfoObject.mousePositionDelta.y;
            }
            if (mouseInfoObject.deltaCallback)
                mouseInfoObject.deltaCallback();

            mouseInfoObject.mousePosition.x = e.pageX;
            mouseInfoObject.mousePosition.y = e.pageY;

            if (e.currentTarget == window) {
                mouseInfoObject.localMousePosition.x = 0; // ((mouseInfoObject.mousePosition.x - $frame.offset().left)/internalZoom) + $fContent().scrollLeft();
                mouseInfoObject.localMousePosition.y = 0; // ((mouseInfoObject.mousePosition.y - $frame.offset().top )/internalZoom) + $fContent().scrollTop();
                $('#debug-thingy-positionInfoValue').html('x: <span class="text-right">-</span><br>y: <span class="text-right">-</span>');
            } else {
                mouseInfoObject.localMousePosition.x = (mouseInfoObject.mousePosition.x);
                mouseInfoObject.localMousePosition.y = (mouseInfoObject.mousePosition.y);
                $('#debug-thingy-positionInfoValue').html('x: <span class="text-right">' + Math.floor(mouseInfoObject.localMousePosition.x / internalZoom) + '</span><br>y: <span class="text-right">' + Math.floor(mouseInfoObject.localMousePosition.y / internalZoom) + '</span>');
            }
        }

        function toggleStyleSheets() {

            /**
             *  toggle CSS StyleSheets, inline-styles and styling node attributes
             **/

            function toggleAttribute(node, attribute) {
                if (node.getAttribute(cssDisabled ? attribute : 'data-disabledAttribute-' + attribute) !== null) {
                    node.setAttribute(!cssDisabled ? attribute : 'data-disabledAttribute-' + attribute, node.getAttribute(cssDisabled ? attribute : 'data-disabledAttribute-' + attribute));
                    node.removeAttribute(cssDisabled ? attribute : 'data-disabledAttribute-' + attribute);
                }
            }

            var attr = {
                APPLET: ['align', 'height', 'hspace', 'vspace', 'width'],
                BASEFONT: ['color', 'face', 'size'],
                BODY: ['alink', 'background', 'bgcolor', 'link', 'text', 'vlink'],
                BR: ['clear'],
                CAPTION: ['align'],
                COL: ['align', 'char', 'charoff', 'span', 'valign', 'width'],
                COLGROUP: ['align', 'valign', 'width'],
                DIR: ['compact'],
                DIV: ['align'],
                DL: ['compact'],
                EMBED: ['width', 'height'],
                FONT: ['color', 'face', 'size'],
                IFRAME: ['align', 'frameborder', 'height', 'marginwidth', 'marginheight', 'scrolling', 'width'],
                IMG: ['align', 'border', 'height', 'hspace', 'vspace', 'width'],
                INPUT: ['align', 'size'],
                LEGEND: ['align'],
                MENU: ['compact'],
                OBJECT: ['align', 'border', 'height', 'hspace', 'vspace', 'width'],
                P: ['align'],
                PRE: ['width'],
                SELECT: ['size'],
                STYLE: ['media'],
                TABLE: ['align', 'border', 'bgcolor', 'cellpadding', 'cellspacing', 'width'],
                TBODY: ['align', 'valign'],
                TD: ['align', 'bgcolor', 'colspan', 'height', 'nowrap', 'rowspan', 'valign', 'width'],
                TFOOT: ['align', 'valign'],
                TH: ['align', 'bgcolor', 'colspan', 'height', 'nowrap', 'rowspan', 'valign', 'width'],
                THEAD: ['align', 'valign'],
                TR: ['align', 'valign'],
                UL: ['compact', 'type'],
                VIDEO: ['width', 'height']
            };

            var i = 0;
            var j = 0;

            var cssDisabled = $fContent()[0].body.getAttribute('data-cssDisabled');

            if (cssDisabled === 'true') {
                cssDisabled = false;
                $fContent()[0].body.removeAttribute('data-cssDisabled');
                $fContent().first().find('body .tc-link-location').remove();
            } else {
                cssDisabled = true;
                $fContent()[0].body.setAttribute('data-cssDisabled', true);
                $fContent().first().find('body a').showLinkLocation();
            }

            /* toggle CSS StyleSheets */
            for (i = $fContent()[0].styleSheets.length - 1; i >= 0; --i) {
                if ($fContent()[0].styleSheets[i].href !== null)
                    $fContent()[0].styleSheets[i].disabled = cssDisabled;
            }

            /* toggle inline-styles and tag associated styling attributes */
            var nodes = $fContent()[0].getElementsByTagName('*');
            for (i = nodes.length - 1; i >= 0; --i) {
                var n = nodes[i];
//        if (n instanceof Node) {
                toggleAttribute(n, 'style');
                var al = attr[n.nodeName];
                if (al) {
                    for (j = al.length - 1; j >= 0; --j) {
                        toggleAttribute(n, al[j]);
                    }
                }
//        }
            }
        }


        $(window).mousemove(this, mouseMoveHandler);

        var pipetteActive = false;

        function togglePipette(e) {
            e.preventDefault();
            e.stopPropagation();

            pipetteActive = !pipetteActive;
            if (pipetteActive) {
                if (measureRectActive)
                    toggleMeasureRect();
                $('html').addClass('noselect cursor-crosshair');
                $($fContent()).find('body').addClass('noselect cursor-crosshair');
                $($fContent()).bind('mousemove', pipetteMouseMoveHandler);
                $($fContent()).find('body').click(this, togglePipette);
                $('#debug-thingy-colorPicker').addClass('btn-positive').removeClass('btn-default');
            } else {
                $('html').removeClass('noselect cursor-crosshair');
                $($fContent()).find('body').removeClass('noselect cursor-crosshair');
                $($fContent()).unbind('mousemove', pipetteMouseMoveHandler);
                $($fContent()).find('body').unbind('click', togglePipette);
                $('#debug-thingy-colorPicker').removeClass('btn-positive').addClass('btn-default');
            }
        }

        function pipetteMouseMoveHandler(e) {
            var el = $fContent()[0].elementFromPoint(mouseInfoObject.localMousePosition.x - $($fContent()).scrollLeft(), mouseInfoObject.localMousePosition.y - $($fContent()).scrollTop());

            var selfIntersection = false;
            if (el && !selfIntersection) {
//        $('#debug-thingy-pipetteInfo').text(e.pageX +" "+ e.pageY +" "+ e.offsetX +" "+ e.offsetY +" "+ el.toString());
                $('#debug-thingy-pipetteInfo-colorSwatch').css({
                    'background-color': $(el).css('background-color'),
                    'border-color': $(el).css('border-color'),
                    color: $(el).css('color')
                });

                $('#debug-thingy-pipetteInfo-colorValue').text($(el).css('color'));
                $('#debug-thingy-pipetteInfo-backgroundColorValue').text($(el).css('background-color'));
                $('#debug-thingy-pipetteInfo-borderColorValue').text($(el).css('border-color'));
            }
        }

        var measureRectActive = false;

        function toggleMeasureRect(e) {
            measureRectActive = !measureRectActive;

            if (measureRectInternal === null || e.type === 'dblclick') {
                var w = Math.min(100, $(window).width());
                var h = Math.min(150, $(window).height());

                measureRectInternal = {
                    x: Math.round((($(window).width() - w) * 0.5) / internalZoom),
                    y: Math.round((($(window).height() - h) * 0.5) / internalZoom),
                    width: w,
                    height: h
                };

                if (zoom !== 1) {
                    measureRectInternal.x += Math.round($(window).scrollLeft() / internalZoom);
                    measureRectInternal.y += Math.round($(window).scrollTop() / internalZoom);
                }
            }

            if (measureRectActive) {
                if (pipetteActive)
                    togglePipette();

                $('body').append(
                    '<div class="measure-rect">' +
                    '<div class="measure-rect-background top"/>' +
                    '<div class="measure-rect-background right"/>' +
                    '<div class="measure-rect-background bottom"/>' +
                    '<div class="measure-rect-background left"/>' +
                    '<div class="measure-rect-border"/>' +
                    '<div class="measure-rect-thumb top left"/>' +
                    '<div class="measure-rect-thumb top right"/>' +
                    '<div class="measure-rect-thumb bottom left"/>' +
                    '<div class="measure-rect-thumb bottom right"/>' +
                    '</div>'
                );
                updateMeasureRect();
                $('.measure-rect .measure-rect-border').mousedown(this, measureRectBorderMouseDownHandler);
                $('.measure-rect .measure-rect-thumb').mousedown(this, measureRectThumbMouseDownHandler);
                $('#debug-thingy-measureRect').addClass('btn-positive').removeClass('btn-default');
            } else {
                $('.measure-rect').remove();
                $('.measure-rect .measure-rect-border').unbind();
                $('.measure-rect .measure-rect-thumb').unbind();
                $('#debug-thingy-measureRect').removeClass('btn-positive').addClass('btn-default');
            }

            $('#measure-rect-info').css({display: measureRectActive ? 'block' : 'none'});
        }

        var measureRect = {};
        var measureRectInternal = null;

        var mouseInfoObject = {
            currentMousePosition: {x: 0, y: 0},
            mousePosition: {x: 0, y: 0},
            localMousePosition: {x: 0, y: 0},
            mousePositionDelta: {x: 0, y: 0},
            dragObject: null,
            deltaCallback: null,
            finishCallback: null
        };

        var targetMeasureRectThumb;

        function measureRectBorderMouseDownHandler(e) {
            mouseInfoObject.mousePosition = {
                x: Math.round(e.pageX / internalZoom),
                y: Math.round(e.pageY / internalZoom)
            };
            startDrag(measureRectInternal, updateMeasureRect);
        }

        function startDrag(dragObject, deltaCallback, finishCallback) {
            mouseInfoObject.dragObject = dragObject;
            mouseInfoObject.deltaCallback = deltaCallback;
            mouseInfoObject.finishCallback = finishCallback;
            $('body').addClass('noselect');
//      console.log($('body .mouse-move-event-target-dummy'))
            if (!$('body .mouse-move-event-target-dummy').length)
                $('body').append('<div class="mouse-move-event-target-dummy"/>');
            $('body').bind('mouseup', dragDropMouseUpHandler);
        }

        function dragDropMouseUpHandler(e) {
            stopDrag();
        }

        function stopDrag() {
            if (mouseInfoObject.finishCallback) {
                mouseInfoObject.finishCallback();
            }

            mouseInfoObject.dragObject = null;
            mouseInfoObject.deltaCallback = null;
            mouseInfoObject.finishCallback = null;
            $('body').removeClass('noselect');
            $('.mouse-move-event-target-dummy').remove();
            $('body').unbind('mouseup', dragDropMouseUpHandler);
        }

        function measureRectThumbMouseDownHandler(e) {
            targetMeasureRectThumb = e.currentTarget;
            startDrag(null, measureRectThumbDragDelta, measureRectThumbDragComplete)
        }

        function measureRectThumbDragDelta() {
            var $e = $(targetMeasureRectThumb);
            if ($e.hasClass('top')) {
                measureRectInternal.y += mouseInfoObject.mousePositionDelta.y;
                measureRectInternal.height -= mouseInfoObject.mousePositionDelta.y;
            }
            if ($e.hasClass('bottom')) {
                measureRectInternal.height += mouseInfoObject.mousePositionDelta.y;
            }
            if ($e.hasClass('left')) {
                measureRectInternal.x += mouseInfoObject.mousePositionDelta.x;
                measureRectInternal.width -= mouseInfoObject.mousePositionDelta.x;
            }
            if ($e.hasClass('right')) {
                measureRectInternal.width += mouseInfoObject.mousePositionDelta.x;
            }
            updateMeasureRect();
        }

        function measureRectThumbDragComplete() {
            targetMeasureRectThumb = null;
            measureRectInternal.x = measureRect.x;
            measureRectInternal.y = measureRect.y;
            measureRectInternal.width = measureRect.width;
            measureRectInternal.height = measureRect.height;
        }

        function updateMeasureRect() {

            measureRectInternal.left = measureRect.x = measureRectInternal.width >= 0 ? measureRectInternal.x : measureRectInternal.x + measureRectInternal.width;
            measureRectInternal.top = measureRect.y = measureRectInternal.height >= 0 ? measureRectInternal.y : measureRectInternal.y + measureRectInternal.height;
            measureRectInternal.right = measureRectInternal.left + Math.abs(measureRectInternal.width);
            measureRectInternal.bottom = measureRectInternal.top + Math.abs(measureRectInternal.height);

            measureRect.width = Math.abs(measureRectInternal.width);
            measureRect.height = Math.abs(measureRectInternal.height);

            $('.measure-rect .measure-rect-border').css({
                left: measureRectInternal.left,
                top: measureRectInternal.top,
                width: measureRect.width,
                height: measureRect.height
            });

            var thumbHalfSize = 10;
            var thumbOffsetH = measureRect.width > 40 ? 0.5 * thumbHalfSize : -1 * thumbHalfSize;
            var thumbOffsetV = measureRect.height > 40 ? 0.5 * thumbHalfSize : -1 * thumbHalfSize;

            $('.measure-rect .measure-rect-thumb.top.left').css({
                left: measureRectInternal.left - thumbHalfSize + thumbOffsetH,
                top: measureRectInternal.top - thumbHalfSize + thumbOffsetV
            });
            $('.measure-rect .measure-rect-thumb.top.right').css({
                left: measureRectInternal.right - thumbHalfSize - thumbOffsetH,
                top: measureRectInternal.top - thumbHalfSize + thumbOffsetV
            });
            $('.measure-rect .measure-rect-thumb.bottom.left').css({
                left: measureRectInternal.left - thumbHalfSize + thumbOffsetH,
                top: measureRectInternal.bottom - thumbHalfSize - thumbOffsetV
            });
            $('.measure-rect .measure-rect-thumb.bottom.right').css({
                left: measureRectInternal.right - thumbHalfSize - thumbOffsetH,
                top: measureRectInternal.bottom - thumbHalfSize - thumbOffsetV
            });

            $('.measure-rect .measure-rect-background.top').css({
                top: 0,
                left: 0,
                right: 0,
                bottom: 'auto',
                height: measureRectInternal.top
            });
            $('.measure-rect .measure-rect-background.bottom').css({
                top: measureRectInternal.bottom,
                left: 0,
                right: 0,
                bottom: 0
            });
            $('.measure-rect .measure-rect-background.left').css({
                top: measureRectInternal.top,
                left: 0,
                right: 'auto',
                bottom: 'auto',
                height: measureRect.height,
                width: measureRectInternal.left
            });
            $('.measure-rect .measure-rect-background.right').css({
                top: measureRectInternal.top,
                left: measureRectInternal.right,
                right: 0,
                bottom: 'auto',
                height: measureRect.height
            });

            $('#measure-rect-info-x').text((((measureRect.x - $frame.offset().left) / zoom) + $fContent().scrollLeft()).toFixed(2));
            $('#measure-rect-info-y').text((((measureRect.y - $frame.offset().top) / zoom) + $fContent().scrollTop()).toFixed(2));
            $('#measure-rect-info-width').text((measureRect.width / zoom).toFixed(2));
            $('#measure-rect-info-height').text((measureRect.height / zoom).toFixed(2));
        }

        /** ************ UI listeners **************/

        var windowPosition = {x: 0, y: 0};

        function updateWindowPosition() {
            $('#debug-thingy').css({
                left: windowPosition.x - $(window).scrollLeft(),
                top: windowPosition.y - $(window).scrollTop(),
                right: 'auto',
                bottom: 'auto'
            });
        }

        function lockWindowPosition() {
            var distanceLeft = windowPosition.x - $(window).scrollLeft();
            var distanceTop = windowPosition.y - $(window).scrollTop();
            var distanceRight = $(window).width() + $(window).scrollLeft() - windowPosition.x - $('#debug-thingy').outerWidth();
            var distanceBottom = $(window).height() + $(window).scrollTop() - windowPosition.y - $('#debug-thingy').outerHeight();
            var allignLeft = Math.abs(distanceLeft) < Math.abs(distanceRight);
            var allignTop = Math.abs(distanceTop) < Math.abs(distanceBottom);
            $('#debug-thingy').css(
                {
                    left: allignLeft ? distanceLeft : 'auto',
                    top: allignTop ? distanceTop : 'auto',
                    right: allignLeft ? 'auto' : distanceRight,
                    bottom: allignTop ? 'auto' : distanceBottom
                }
            );
        }

        $('.size-o-tron').mousedown(this, function () {
            windowPosition.x = $('#debug-thingy').offset().left / internalZoom;
            windowPosition.y = $('#debug-thingy').offset().top / internalZoom;
            startDrag(windowPosition, updateWindowPosition, lockWindowPosition);
        })

        $('#debug-thingy-imageOverlayToggleOpenSettings').click(this, imageOverlayToggleOpenSettings);
        $('#debug-thingy-toggleGridVisibility').click(this, toggleGridVisibility);
        $('#debug-thingy-toggleCrossSectionVisibility').click(this, toggleCrossSectionGrid);
        $('#debug-thingy-toggleCrossSectionVisibility').dblclick(this, toggleCrossSectionOverlay);
        $('#debug-thingy-toggleBreakpoints').click(this, toggleBreakpointsOnResize);

        $('#debug-thingy-toggleStyleSheets').click(this, toggleStyleSheets);
        $('#debug-thingy-colorPicker').click(this, togglePipette);
        $('#debug-thingy-measureRect').click(this, toggleMeasureRect);
        $('#debug-thingy-measureRect').dblclick(this, toggleMeasureRect);


        $('#debug-thingy-togglePageLock').click(this, togglePageLockHandler);

        $('#debug-thingy-btnHide').click(this, DebugThingy.prototype.toggleDebugThingy);

        $('#debug-thingy-imageOverlayToggleActivity').click(this, imageOverlayToggleActivity);
        $('#debug-thingy-imageOverlayToggleAlignment').click(this, imageOverlayToggleAlignment);
        $('#debug-thingy-imageOverlayToggleStyle').click(this, imageOverlayToggleStyle);
        $('#debug-thingy-imageOverlayToggleFixed').click(this, imageOverlayToggleFixed);
        $('#debug-thingy-imageOverlayAlphaSlider').on('input', this, imageOverlayAlphaChange);
        $('#debug-thingy-imageOverlayAlphaSlider').change(this, imageOverlayAlphaChange);


        $('#debug-thingy-pageZoomSlider').on('input', this, pageZoomSliderInput);
        $('#debug-thingy-pageZoomSlider').change(this, pageZoomSliderChange);

        $('#debug-thingy-transformX, #debug-thingy-transformY, #debug-thingy-transformScale').on('change keydown keyup input', this, imageOverlayTransformChange);

        $('#debug-thingy-image-overlay-canvas').change(function () {
            readFile(this);
        });

        $('#debug-thingy-image-overlay-canvas-delete').click(function () {
            window.sessionStorage.setItem('debug-image-overlay-file-data', null);
            $('.debug-image-overlay').remove();
            // replace file-input to clear it's value, allowing to pick the same file again
            var control = $('#debug-thingy-image-overlay-canvas');
            control.replaceWith(control = control.clone(true));
        });

        $('#debug-thingy-image-overlay').change(function () {
            readURL(this);
        });

        $('#debug-thingy-image-overlay-filename').change(function () {
            window.sessionStorage.setItem('debug-thingy-image-overlay-file-name', $(this).val());
        })

        $('#debug-thingy-image-overlay-filename').val(window.sessionStorage.getItem('debug-thingy-image-overlay-file-name'));

        $('#debug-thingy-image-overlay-path').val(window.sessionStorage.getItem('debug-thingy-image-overlay-path'));

        $('#debug-thingy-image-overlay-path').change(function () {
            var str = $('#debug-thingy-image-overlay-path').val();
            var path = str.replace(/\\/g, '/');
            path = path.replace(/(\/*$)/, '/');
            if (path !== str)
                $('#debug-thingy-image-overlay-path').val(path);
            window.sessionStorage.setItem('debug-thingy-image-overlay-path', path);
            readURL();
        });

        $('#debug-thingy-imageOverlayCustomCSS').change(this, imageOverlayCustomCSSChange);

        // only for demo page
        if ($('.demo-grid').length > 0) {
            $('#debug-thingy-toggleDemoGrids').click(this, toggleDemoGrids);
            $('#debug-thingy-toggleCrossSectionDemoGrids').click(this, toggleCrossSectionDemoGrids);
        } else {
            $('.debug-thingy-demo-buttons').remove();
        }


        var debugThingySizeInfo = $('#debug-thingy .size-info');
        var debugThingySizeOTronBar = $('#debug-thingy .size-o-tron .size-bar');

        function sizeInfo() {
            var pdr = window.devicePixelRatio || 1;

            $(debugThingySizeInfo).html(Math.round(screenSize.width) + ' x ~' + Math.round(screenSize.height) + (pdr > 1 ? ' (x' + pdr + ')' : ''));
            $(debugThingySizeOTronBar).width(100 * screenSize.width / screenMaxWidth + '%');

            if (DebugThingy.prototype.settings.breakpoints)
                $('.debug-breakpoint-thingy').finish().show(0).delay(800).fadeOut(3000);
        }

        var dynCSS = '';
        var screenMaxWidth;

        function setGridTronBreakPoints() {
            var styleSheet = getComponentStyleSheet();
            var brpts = getBreakpoints(styleSheet);

            var arr = ['xs', 's', 'm', 'l', 'xl'];
            // console.log(screen.availWidth, screen.availHeight, window.innerWidth, window.innerHeight, $(window).width(), $(window).height(), brpts[brpts.length - 1])
            screenMaxWidth = Math.max(screen.availWidth, screen.availHeight, $(window).width(), $(window).height(), brpts[brpts.length - 1]);
            if (window.innerWidth && window.innerHeight)
                screenMaxWidth = Math.max(screenMaxWidth, window.innerWidth, window.innerHeight);
            for (var i = 0; i < arr.length; ++i) {
                $('#debug-thingy .size-o-tron').append('<div class="screensize bp-' + arr[i] + ' col-' + arr[i] + '-emphased">' + arr[i].toUpperCase() + '</div>');
                $('#debug-thingy .size-o-tron .screensize.bp-' + arr[i]).css('left', 100 * brpts[i] / screenMaxWidth + '%');
                if (i < arr.length - 1) {
                    // console.log(brpts[i + 1])
                    // console.log(screenMaxWidth);
                    // console.log(100 - Math.min(100, 100 * (brpts[i + 1] / screenMaxWidth)))
                    $('#debug-thingy .size-o-tron .screensize.bp-' + arr[i]).css('right', 100 - Math.min(100, 100 * (brpts[i + 1] / screenMaxWidth)) + '%');
                }
                if (i < arr.length)
                    $('#debug-thingy .size-o-tron .breakpoint.bp-' + arr[i]).css('left', 100 * brpts[i] / screenMaxWidth + '%');

                if (i > 0) {
                    $('body').append('<div class="debug-breakpoint-thingy bp-' + arr[i] + '">&nbsp;</div>');
                    dynCSS += '.debug-breakpoint-thingy.bp-' + arr[i] + ':before { content: "' + arr[i].toUpperCase() + '";} ';
                    dynCSS += '.debug-breakpoint-thingy.bp-' + arr[i] + ':after { font-size: .5em; content: "(' + brpts[i] + 'px)";} ';
                }
            }
        }

        function setGridTronBreakPointPositions() {
            var styleSheet = getComponentStyleSheet();
            var brpts = getBreakpoints(styleSheet);

            var arr = ['xs', 's', 'm', 'l', 'xl'];
            for (var i = 0; i < arr.length; ++i) {
                $('.debug-breakpoint-thingy.bp-' + arr[i]).css({
                    left: ($frame.offset().left + (brpts[i] * zoom)) + 'px',
                    top: $frame.offset().top + 'px'
                });
            }
        }

        setGridTronBreakPoints();
        setGridTronBreakPointPositions();

        $('.demo-grid').each(function (key, val) {
            val.outerHTML = '<div class="demo-grid-wrapper">' + val.outerHTML + '</div>' + '<div class="demo-grid-placeholder" style="height:' + $(val).height() + 'px">&nbsp;</div>';
        });

        var debugThingyCSS = '<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet"><style>' + dynCSS + '</style>';
        $('body').append(debugThingyCSS);

        function demoGridWrapperResizeHandler() {
            if (pageLock)
                return;
            $('.demo-grid-wrapper').each(function (key, val) {
                var h = $(val).find('.demo-grid').height();
                $(val).next('.demo-grid-placeholder').height(h);
            });
        }

        $(window).resize(demoGridWrapperResizeHandler);
//    $(window).resize(sizeInfo);
//    $(window).on('orientationchange', sizeInfo);

        $(document).ready(function () {
            $('.debug-iframe').ready(function () {
                setTimeout(function () {
                    $('head').append(
                        '<link type="text/css" rel="stylesheet" href="css/components.min.css" />' +
                        '<link type="text/css" rel="stylesheet" href="css/theme-bevel.min.css" />' +
                        '<link type="text/css" rel="stylesheet" href="css/theme-debug.min.css" />'
                    );
                    $('body').css({overflow: 'hidden', background: '#666'});
                    updateScreenSize();
                    sizeInfo();
                    loadSettings();
                    $scrollPane.scroll(iframeScrollHandler);
                    $fContent().scroll(iframeScrollHandler);
                    $($fContent()).mousemove(this, mouseMoveHandler);
                }, 0);
            })
        })
    }

    DebugThingy.prototype.toggleDebugThingy = function () {
        $('#debug-thingy').toggleClass('hidden');
        DebugThingy.prototype.settings.open = !$('#debug-thingy').hasClass('hidden');
        window.sessionStorage.setItem('debug-thingy-settings', JSON.stringify(DebugThingy.prototype.settings));
    }

    DebugThingy.prototype.getDefaults = function () {
        return DebugThingy.DEFAULTS;
    }

    DebugThingy.prototype.getOptions = function (options) {
        options = $.extend({}, this.getDefaults(), this.$element.data(), options);
        return options;
    }

    DebugThingy.prototype.debugThingy = function () {
        return this.$debugThingy = this.$debugThingy || $(this.options.template);
    }


    // DEBUG THINGY PLUGIN DEFINITION
    // ==========================

    function Plugin(option) {
        return this.each(function () {

            var $this = $(this);
            var data = $this.data('tc.debugThingy');
            var options = typeof option === 'object' && option;

            if (!$('#debug-thingy').length) {
                $this.data('tc.debugThingy', (data = new DebugThingy(this, options)));
            } else {
                DebugThingy.prototype.toggleDebugThingy();
            }
            if (typeof option === 'string')
                data[option].call($this);

        });
    }

    var old = $.fn.debugThingy

    $.fn.debugThingy = Plugin
    $.fn.debugThingy.Constructor = DebugThingy


    // CHECKBOX NO CONFLICT
    // ====================

    $.fn.checkbox.noConflict = function () {
        $.fn.checkbox = old;
        return this;
    };

}(window.jQuery);


+function ($) {
    $.fn.showLinkLocation = function () {
        this.filter('a').each(function () {
            var link = $(this);
            link.append('<span class="tc-link-location"> (' + link.attr('href') + ')</span>');
        });

        return this;
    };
}(window.jQuery);
