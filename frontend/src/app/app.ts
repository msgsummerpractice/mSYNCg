import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toolbar } from './shared/components/containers/toolbar-component';
import { UserIcon } from './features/user/components/containers/user-icon-component';
import { Button } from './shared/components/containers/button-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toolbar, UserIcon, Button],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
