import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'updateNarucitelj/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'updateRadniNalog/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'updateAktivnost/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'narucitelj/:id',
    renderMode: RenderMode.Server
  },
  {
    path: 'radni-nalog/:id',
    renderMode: RenderMode.Server
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender
  }
];
