(function() {
  var page    = decodeURIComponent(location.pathname.split('/').pop() || 'index.html');
  var sidebar = document.querySelector('.sidebar-nav');
  if (!sidebar) return;

  // ── Toggle handlers ───────────────────────────────────────────────────────
  sidebar.querySelectorAll('.sidebar-toggle').forEach(function(btn) {
    btn.addEventListener('click', function() {
      var targetId = this.getAttribute('data-target');
      var list = document.getElementById(targetId);
      if (!list) return;
      var open = !list.hasAttribute('hidden');
      if (open) {
        list.setAttribute('hidden', '');
        this.setAttribute('aria-expanded', 'false');
        this.querySelector('.chevron').textContent = '\u25b8';
      } else {
        list.removeAttribute('hidden');
        this.setAttribute('aria-expanded', 'true');
        this.querySelector('.chevron').textContent = '\u25be';
      }
    });
  });

  // ── Find active link ──────────────────────────────────────────────────────
  var allLinks = sidebar.querySelectorAll('a[href]');
  var activeLink = null;
  for (var i = 0; i < allLinks.length; i++) {
    if (allLinks[i].getAttribute('href') === page) {
      activeLink = allLinks[i];
      break;
    }
  }
  if (activeLink) activeLink.classList.add('active');

  // ── Auto-expand: walk up DOM, open all hidden parent lists ────────────────
  function expandAncestors(el) {
    var node = el.parentElement;
    while (node && node !== sidebar) {
      if (node.tagName === 'UL' && node.hasAttribute('hidden')) {
        node.removeAttribute('hidden');
        var btn = sidebar.querySelector('[data-target="' + node.id + '"]');
        if (btn) {
          btn.setAttribute('aria-expanded', 'true');
          btn.querySelector('.chevron').textContent = '\u25be';
        }
      }
      node = node.parentElement;
    }
  }
  if (activeLink) expandAncestors(activeLink);

  // ── Move Pandoc TOC into sidebar ──────────────────────────────────────────
  var toc = document.querySelector('nav#TOC, div#TOC');
  if (!toc) return;

  // Pandoc wraps all items under one top-level <li> for the page heading.
  // Skip that wrapper; inject only the sub-headings beneath it.
  var topUl   = toc.querySelector('ul');
  var firstLi = topUl && topUl.querySelector('li');
  var innerUl = firstLi && firstLi.querySelector('ul');

  toc.style.display = 'none';

  if (!innerUl) return;

  var subNav = innerUl.cloneNode(true);
  subNav.className = 'sidebar-sub';
  subNav.querySelectorAll('[id]').forEach(function(el) { el.removeAttribute('id'); });

  if (activeLink) {
    activeLink.parentElement.appendChild(subNav);
  } else {
    var slot  = document.getElementById('sidebar-toc');
    var label = document.getElementById('sidebar-toc-label');
    if (slot)  { slot.appendChild(subNav); }
    if (label) { label.removeAttribute('hidden'); }
  }

  // ── Scroll-spy ────────────────────────────────────────────────────────────
  var headings   = Array.from(document.querySelectorAll('h1[id],h2[id],h3[id],h4[id]'));
  var subLinks   = sidebar.querySelectorAll('.sidebar-sub a[href^="#"]');
  if (!headings.length || !subLinks.length) return;

  var headingIds  = headings.map(function(h) { return h.getAttribute('id'); });
  var subLinkMap  = new Map();
  subLinks.forEach(function(a) { subLinkMap.set(a.getAttribute('href'), a); });

  var observer = new IntersectionObserver(function(entries) {
    entries.forEach(function(entry) {
      if (!entry.isIntersecting) return;
      var id  = entry.target.getAttribute('id');
      var target = null;
      var idx = headingIds.indexOf(id);
      for (var j = idx; j >= 0; j--) {
        target = subLinkMap.get('#' + headingIds[j]);
        if (target) break;
      }
      if (target) {
        subLinks.forEach(function(a) { a.classList.remove('active'); });
        target.classList.add('active');
      }
    });
  }, { rootMargin: '-10% 0px -80% 0px', threshold: 0 });

  headings.forEach(function(h) { observer.observe(h); });
})();

// ── Lightbox for «↗ Vollbild» links and content images ───────────────────────
document.addEventListener('DOMContentLoaded', function () {
  function openLightbox(src) {
    var lb = document.createElement('div');
    lb.id = 'cp-lightbox';
    var inner = document.createElement('div');
    inner.className = 'cp-lb-inner';
    var img = document.createElement('img');
    img.src = src;
    inner.appendChild(img);
    lb.appendChild(inner);
    document.body.appendChild(lb);
    lb.addEventListener('click', function (ev) {
      if (ev.target === lb || ev.target === img) document.body.removeChild(lb);
    });
    function onKey(ev) {
      if (ev.key === 'Escape' && document.getElementById('cp-lightbox')) {
        document.body.removeChild(lb);
        document.removeEventListener('keydown', onKey);
      }
    }
    document.addEventListener('keydown', onKey);
  }

  document.querySelectorAll('.img-link a').forEach(function (link) {
    link.addEventListener('click', function (e) {
      e.preventDefault();
      openLightbox(link.getAttribute('href'));
    });
  });

  var contentRoot = document.querySelector('main') || document.querySelector('.content') || document.body;
  contentRoot.addEventListener('click', function (ev) {
    var img = ev.target;
    if (img.tagName !== 'IMG' || img.closest('#cp-lightbox')) return;
    openLightbox(img.getAttribute('src'));
  });
});
