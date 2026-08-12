import { Component, inject } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';
import { Router } from '@angular/router';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView],
  template: `<app-toolbar-view (navigate)="navigate($event)"></app-toolbar-view>`,
  styleUrl: '../views/toolbar/toolbar.view.scss',
})
export class ToolbarContainer {
  private router = inject(Router);

  navigate(route: string): void {
    this.router.navigate([route]);
  }
}
