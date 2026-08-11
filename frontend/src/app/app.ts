import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToolbarComponent } from './shared/components/containers/toolbar-component';
import { UserIconComponent } from './features/user/components/containers/user-icon-component';
import { ButtonComponent } from './shared/components/containers/button-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToolbarComponent, UserIconComponent, ButtonComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
