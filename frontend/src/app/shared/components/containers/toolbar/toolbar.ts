import { Component } from '@angular/core';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import { UserIcon } from '../../../../features/user/components/containers/user-icon/user-icon';
import { Button } from '../../../../shared/components/containers/button/button';

@Component({
  selector: 'app-toolbar-component',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, UserIcon, Button],
  templateUrl: '../../views/toolbar/toolbar.html',
  styleUrl: '../../views/toolbar/toolbar.css',
})
export class Toolbar {

}
