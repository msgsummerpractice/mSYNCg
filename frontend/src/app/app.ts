import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toolbar } from './shared/components/containers/toolbar/toolbar';
import { UserIcon } from './features/user/components/containers/user-icon/user-icon';
import { Button } from './shared/components/containers/button/button';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toolbar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
