import { Component } from '@angular/core';
import { ToolbarView } from '../views/toolbar/toolbar.view';

@Component({
  selector: 'app-toolbar-container',
  imports: [ToolbarView],
  template: `<app-toolbar-view></app-toolbar-view>`,
  styleUrl: '../views/toolbar/toolbar.view.css',
})
export class Toolbar{

}
