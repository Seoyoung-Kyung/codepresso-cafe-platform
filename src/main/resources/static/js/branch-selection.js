(function (window) {
    if (!window) {
        return;
    }

    var STORAGE_KEY = 'cpSelectedBranch';

    function safeParse(value) {
        if (!value) {
            return null;
        }
        try {
            return JSON.parse(value);
        } catch (error) {
            return null;
        }
    }

    function normalize(selection) {
        if (!selection || typeof selection !== 'object') {
            return null;
        }
        var id = selection.id != null ? String(selection.id).trim() : '';
        if (!id) {
            return null;
        }
        return {
            id: id,
            name: selection.name != null ? String(selection.name).trim() : '',
            address: selection.address != null ? String(selection.address).trim() : '',
            openingTime: selection.openingTime != null ? String(selection.openingTime).trim() : '',
            closingTime: selection.closingTime != null ? String(selection.closingTime).trim() : ''
        };
    }

    function notify(selection) {
        try {
            var event = new CustomEvent('branchSelection:change', { detail: selection });
            window.dispatchEvent(event);
        } catch (error) {
            // IE11 CustomEvent polyfill 등은 현재 지원 대상 아님
        }
    }

    function save(selection) {
        var normalized = normalize(selection);
        if (!normalized) {
            clear();
            return;
        }
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(normalized));
            notify(normalized);
        } catch (error) {
            // 저장 실패 시 무시
        }
    }

    function load() {
        return normalize(safeParse(localStorage.getItem(STORAGE_KEY)));
    }

    function clear() {
        try {
            localStorage.removeItem(STORAGE_KEY);
            notify(null);
        } catch (error) {
            // noop
        }
    }

    window.branchSelection = {
        save: save,
        load: load,
        clear: clear,
        STORAGE_KEY: STORAGE_KEY
    };

    window.addEventListener('storage', function (event) {
        if (event && event.key === STORAGE_KEY) {
            notify(load());
        }
    });
})(typeof window !== 'undefined' ? window : null);
