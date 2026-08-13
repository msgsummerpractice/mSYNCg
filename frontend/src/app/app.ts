import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToolbarContainer } from './shared/components/containers/toolbar.container';
import { ToastContainer } from './shared/components/containers/toast.container';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToolbarContainer,ToastContainer],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');
}
