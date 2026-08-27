import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'eventcard/:id',
    renderMode: RenderMode.Server,
  },
  {
    path: 'events/update/:id',
    renderMode: RenderMode.Server,
  },
  {
    path: 'events/:id/register',
    renderMode: RenderMode.Server,
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender,
  },
];