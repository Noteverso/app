export interface BaseNote {
  content: string;
  labels: { labelId: string; name: string }[];
  project: { projectId: string; name: string };
}

export interface FullNote extends BaseNote {
  noteId: string;
  addedAt: string;
  updatedAt: string;
  isArchived: 0 | 1;
  isDeleted: 0 | 1;
  isPinned: 0 | 1;
  attachmentCount: number | null;
  referencedCount: number | null;
  referencingCount: number | null;
  creator: string;
}

export interface NewNote {
  content: string;
  projectId: string;
  labels?: string[];
  files?: string[];
  linkedNotes?: string[];
}

// 笔记分页请求
export type NotePageRequestParams = {
  objectId?: string;
  pageSize: number;
  pageIndex: number;
}

// 笔记分页响应
export type NotePageLoaderData = {
  pageIndex: number;
  pageSize: number;
  total: number;
  records: FullNote[]
}
