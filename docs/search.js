(function () {
  var input   = document.getElementById('search-input');
  var panel   = document.getElementById('search-results');
  if (!input || !panel) return;

  var lunrIdx, docs;

  fetch('search-index.json')
    .then(function (r) { return r.json(); })
    .then(function (data) {
      docs = data;
      lunrIdx = lunr(function () {
        this.ref('id');
        this.field('title', { boost: 10 });
        this.field('body');
        data.forEach(function (d) { this.add(d); }, this);
      });
    })
    .catch(function () { /* search unavailable */ });

  input.addEventListener('input', function () {
    var q = input.value.trim();
    panel.hidden = !q;
    if (!q || !lunrIdx) return;

    var hits = lunrIdx.search(q + '*').slice(0, 8);
    while (panel.firstChild) panel.removeChild(panel.firstChild);

    if (hits.length === 0) {
      var empty = document.createElement('div');
      empty.className = 'search-result-item';
      var msg = document.createElement('span');
      msg.className = 'search-result-excerpt';
      msg.textContent = 'Keine Ergebnisse';
      empty.appendChild(msg);
      panel.appendChild(empty);
      return;
    }

    hits.forEach(function (h) {
      var doc = docs.find(function (d) { return d.id === h.ref; });
      if (!doc) return;

      var item    = document.createElement('div');
      item.className = 'search-result-item';

      var link    = document.createElement('a');
      link.href   = doc.id;

      var titleEl = document.createElement('div');
      titleEl.className   = 'search-result-title';
      titleEl.textContent = doc.title;

      var excerptEl = document.createElement('div');
      excerptEl.className   = 'search-result-excerpt';
      excerptEl.textContent = doc.body.substring(0, 100).replace(/\s+/g, ' ') + '\u2026';

      link.appendChild(titleEl);
      link.appendChild(excerptEl);
      item.appendChild(link);
      panel.appendChild(item);
    });
  });

  document.addEventListener('click', function (e) {
    if (!panel.contains(e.target) && e.target !== input) panel.hidden = true;
  });
}());
