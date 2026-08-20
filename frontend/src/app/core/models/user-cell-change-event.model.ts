import {User} from './user.model';
import {UserRole} from '../constants/role.constant';
import {CellChangeEvent} from './cell-change-event.model';


export type UserCellChangeEvent =
    | CellChangeEvent<User, 'role', UserRole>
    | CellChangeEvent<User, 'status', boolean>;