import { Component } from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { UserIconComponent } from '../../../features/user/components/containers/user-icon-component';
import { ButtonComponent } from '../containers/button-component';

@Component({
  selector: 'app-toolbar-component',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, RouterLink, UserIconComponent, ButtonComponent],
  templateUrl: '../views/toolbar-component.html',
  styleUrl: '../views/toolbar-component.css',
})
export class ToolbarComponent {

}
