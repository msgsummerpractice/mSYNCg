import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToolbarContainer } from '../../components/containers/toolbar.container';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, ToolbarContainer],
  templateUrl: './main-layout.html',
})
export class MainLayoutComponent {}
