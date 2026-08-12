import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Toolbar } from './shared/components/containers/toolbar.container';
import { Button } from './shared/components/containers/button.container';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Toolbar,Button],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
