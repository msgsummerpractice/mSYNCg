import { Component } from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { UserIcon } from '../../../features/user/components/containers/user-icon-component';
import { Button } from '../containers/button-component';

@Component({
  selector: 'app-toolbar-component',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, UserIcon, Button],
  templateUrl: '../views/toolbar-component.html',
  styleUrl: '../views/toolbar-component.css',
})
export class Toolbar {

}
