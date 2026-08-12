import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { UserIcon } from "./shared/components/containers/user-icon.container";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-root',
  imports: [RouterOutlet, UserIcon, MatIconModule, MatToolbarModule, CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('frontend');
}
