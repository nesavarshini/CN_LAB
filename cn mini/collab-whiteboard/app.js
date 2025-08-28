const canvas = document.getElementById('board');
const ctx = canvas.getContext('2d');
const statusEl = document.getElementById('status');
const penBtn = document.getElementById('penBtn');
const eraserBtn = document.getElementById('eraserBtn');
const clearBtn = document.getElementById('clearBtn');
const sizeEl = document.getElementById('size');
const colorEl = document.getElementById('color');

let tool = 'pen'; 
let drawing = false;
let last = null;

function fit() {
  const r = canvas.getBoundingClientRect();
  const dpr = devicePixelRatio || 1;
  canvas.width = Math.round(r.width * dpr);
  canvas.height = Math.round(r.height * dpr);
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
}
fit(); addEventListener('resize', fit);

ctx.lineCap = 'round';
ctx.lineJoin = 'round';

penBtn.onclick = () => { tool = 'pen'; statusEl.textContent = '● pen'; };
eraserBtn.onclick = () => { tool = 'eraser'; statusEl.textContent = '● eraser'; };
clearBtn.onclick = () => send({ type: 'clear' });

canvas.addEventListener('mousedown', e => { drawing = true; last = pos(e); });
canvas.addEventListener('mouseup',   () => { drawing = false; last = null; });
canvas.addEventListener('mouseleave',() => { drawing = false; last = null; });
canvas.addEventListener('mousemove', e => {
  if (!drawing) return;
  const p = pos(e);
  const op = { type: 'stroke', tool, color: colorEl.value, size: +sizeEl.value, a: last, b: p };
  draw(op);     
  send(op);   
  last = p;
});

function pos(e) {
  const r = canvas.getBoundingClientRect();
  return { x: e.clientX - r.left, y: e.clientY - r.top };
}

function draw(op) {
  if (op.type === 'clear') {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    return;
  }
  if (op.type !== 'stroke') return;

  ctx.save();
  ctx.lineWidth = op.size || 6;

  if (op.tool === 'eraser') {
    ctx.globalCompositeOperation = 'destination-out';
    ctx.strokeStyle = 'rgba(0,0,0,1)';
  } else {
    ctx.globalCompositeOperation = 'source-over';
    ctx.strokeStyle = op.color || '#000';
  }

  ctx.beginPath();
  ctx.moveTo(op.a.x, op.a.y);
  ctx.lineTo(op.b.x, op.b.y);
  ctx.stroke();
  ctx.restore();
}

const events = new EventSource('/events');
events.onopen = () => { statusEl.textContent = '● online'; };
events.onerror = () => { statusEl.textContent = '● reconnecting…'; };
events.onmessage = e => {
  try { draw(JSON.parse(e.data)); } catch {}
};

function send(obj) {
  fetch('/draw', { method: 'POST', body: JSON.stringify(obj) })
    .catch(() => {});
}