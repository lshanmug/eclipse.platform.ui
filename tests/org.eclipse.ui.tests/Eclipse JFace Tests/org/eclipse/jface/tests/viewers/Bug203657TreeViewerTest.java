/*******************************************************************************
 * Copyright (c) 2007, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.jface.tests.viewers;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @since 3.3
 *
 */
public class Bug203657TreeViewerTest extends ViewerTestCase {
	public class MyModel {
		public MyModel parent;

		public ArrayList<MyModel> child = new ArrayList<>();

		public int counter;

		public MyModel(int counter, MyModel parent) {
			this.parent = parent;
			this.counter = counter;
		}

		@Override
		public String toString() {
			String rv = "Item ";
			if (parent != null) {
				rv = parent.toString() + ".";
			}

			rv += counter;

			return rv;
		}
	}
	/**
	 * @param name
	 */
	public Bug203657TreeViewerTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected StructuredViewer createViewer(Composite parent) {
		final TreeViewer treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION);

		treeViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public Object[] getElements(Object inputElement) {
				return ((MyModel) inputElement).child.toArray();
			}

			@Override
			public void dispose() {

			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {

			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return getElements(parentElement);
			}

			@Override
			public Object getParent(Object element) {
				if (element == null) {
					return null;
				}

				return ((MyModel) element).parent;
			}

			@Override
			public boolean hasChildren(Object element) {
				return !((MyModel) element).child.isEmpty();
			}
		});

		treeViewer.setCellEditors(new CellEditor[] { new TextCellEditor(
				treeViewer.getTree()) });
		treeViewer.setColumnProperties(new String[] { "0" });
		treeViewer.setCellModifier(new ICellModifier() {
			@Override
			public boolean canModify(Object element, String property) {
				return true;
			}

			@Override
			public Object getValue(Object element, String property) {
				return "";
			}

			@Override
			public void modify(Object element, String property, Object value) {
			}

		});

		new TreeColumn(treeViewer.getTree(), SWT.NONE).setWidth(200);

		return treeViewer;
	}

	@Override
	protected void setUpModel() {
		// don't do anything here - we are not using the normal fModel and
		// fRootElement
	}

	@Override
	protected void setInput() {
		MyModel root = new MyModel(0, null);
		root.counter = 0;

		MyModel tmp;
		for (int i = 1; i < 100; i++) {
			tmp = new MyModel(i, root);
			root.child.add(tmp);
			for (int j = 1; j < i; j++) {
				tmp.child.add(new MyModel(j, tmp));
			}
		}

		getTreeViewer().setInput(root);
	}

	private TreeViewer getTreeViewer() {
		return (TreeViewer) fViewer;
	}

	public void testBug203657() {
		try {
			Field f = ColumnViewer.class.getDeclaredField("cell");
			f.setAccessible(true);
			ViewerCell cell = (ViewerCell) f.get(getTreeViewer());
			assertNull(cell.getElement());
			assertNull(cell.getViewerRow());
			assertEquals(0, cell.getColumnIndex());
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchFieldException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		}
	}


}
